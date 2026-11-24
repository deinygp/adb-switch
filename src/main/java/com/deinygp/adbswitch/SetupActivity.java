package com.deinygp.adbswitch;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

/**
 * Guía de configuración: revisa si el widget puede funcionar en este teléfono y, para cada
 * problema, muestra síntoma y solución con acciones. Se re-evalúa en cada onResume, así el
 * usuario ve el cambio al volver de Ajustes o después de otorgar el permiso por adb.
 */
public class SetupActivity extends Activity {
    private static final int OK = 0, WARN = 1, ERR = 2;
    static final String GRANT_CMD = "adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS";
    private static final String ISSUES_URL = "https://github.com/deinygp/adb-switch/issues";

    private LinearLayout checks;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_setup);
        checks = findViewById(R.id.checks);
        renderFaq();
        requestConsentThenAds();
    }

    // --- Publicidad -------------------------------------------------------------------------

    /** Pide consentimiento (UMP, obligatorio en EEE/UK) y solo entonces carga anuncios. */
    private void requestConsentThenAds() {
        final ConsentInformation ci = UserMessagingPlatform.getConsentInformation(this);
        ci.requestConsentInfoUpdate(this, new ConsentRequestParameters.Builder().build(),
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, err -> {
                    showPrivacyButton(ci);
                    if (ci.canRequestAds()) initAds();
                }),
                err -> {
                    if (ci.canRequestAds()) initAds();
                });
        if (ci.canRequestAds()) initAds(); // consentimiento de una sesión anterior
    }

    private void showPrivacyButton(ConsentInformation ci) {
        if (ci.getPrivacyOptionsRequirementStatus() != ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED) return;
        View b = findViewById(R.id.privacy);
        b.setVisibility(View.VISIBLE);
        b.setOnClickListener(v -> UserMessagingPlatform.showPrivacyOptionsForm(this, err -> { }));
    }

    private boolean adsStarted;

    private void initAds() {
        if (adsStarted) return;
        adsStarted = true;
        new Thread(() -> MobileAds.initialize(this, status -> { })).start();
        int widthDp = (int) (getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density);
        banner(findViewById(R.id.ad_bottom), getString(R.string.ad_unit_bottom),
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, widthDp), false);
        banner(findViewById(R.id.ad_inline_1), getString(R.string.ad_unit_inline), AdSize.BANNER, true);
        banner(findViewById(R.id.ad_inline_2), getString(R.string.ad_unit_inline), AdSize.BANNER, true);
    }

    /** Carga un banner; los intercalados permanecen ocultos (con su etiqueta) hasta que hay anuncio. */
    private void banner(final ViewGroup slot, String unit, AdSize size, final boolean hideUntilLoaded) {
        AdView ad = new AdView(this);
        ad.setAdUnitId(unit);
        ad.setAdSize(size);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                slot.setVisibility(View.VISIBLE);
            }
        });
        slot.addView(ad, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (hideUntilLoaded) slot.setVisibility(View.GONE);
        ad.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderChecks();
        DebugWidget.refresh(this); // de paso, sincroniza el widget con el estado real
    }

    private void renderChecks() {
        checks.removeAllViews();
        boolean perm = DebugWidget.hasPermission(this);
        String svc = DebugWidget.prop("init.svc.adbd");
        boolean stateReadable = !svc.isEmpty();
        boolean on = "running".equals(svc);
        final AppWidgetManager awm = AppWidgetManager.getInstance(this);
        int widgets = awm.getAppWidgetIds(new ComponentName(this, DebugWidget.class)).length;
        int problems = (perm ? 0 : 1) + (stateReadable ? 0 : 1) + (on || !stateReadable ? 0 : 1) + (widgets > 0 ? 0 : 1);

        // Resumen
        View summary = findViewById(R.id.summary);
        TextView sb = findViewById(R.id.summary_badge);
        summary.setBackgroundResource(problems == 0 ? R.drawable.banner_ok : R.drawable.banner_warn);
        sb.setText(problems == 0 ? "✓" : String.valueOf(problems));
        sb.setTextColor(getColor(problems == 0 ? R.color.ok : R.color.warn));
        ((TextView) findViewById(R.id.summary_title)).setText(problems == 0
                ? getString(R.string.summary_ok_title) : getString(R.string.summary_bad_title, problems));
        ((TextView) findViewById(R.id.summary_sub)).setText(problems == 0 ? R.string.summary_ok_sub : R.string.summary_bad_sub);

        // 1. Permiso
        View r = row(perm ? OK : ERR, R.string.perm_title, perm ? R.string.perm_ok : R.string.perm_bad);
        if (!perm) {
            details(r, R.string.perm_symptom, R.string.perm_fix);
            code(r, GRANT_CMD);
            button(r, R.string.perm_copy, true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSystemService(ClipboardManager.class).setPrimaryClip(ClipData.newPlainText("adb", GRANT_CMD));
                    Toast.makeText(SetupActivity.this, R.string.copied, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 2. Lectura del estado
        r = row(stateReadable ? OK : ERR, R.string.state_title,
                !stateReadable ? R.string.state_bad : on ? R.string.state_on : R.string.state_off);
        if (!stateReadable) {
            details(r, R.string.state_symptom, R.string.state_fix);
            button(r, R.string.state_report, true, open(new Intent(Intent.ACTION_VIEW, Uri.parse(ISSUES_URL))));
        }

        // 3. Opciones de desarrollador (Android 17 no deja leerlas: se infiere por adbd corriendo)
        if (stateReadable) {
            r = row(on ? OK : WARN, R.string.dev_title, on ? R.string.dev_ok : R.string.dev_warn);
            if (!on) {
                details(r, R.string.dev_symptom, R.string.dev_fix);
                button(r, R.string.dev_about, true, open(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)));
                button(r, R.string.dev_open, false, open(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)));
            }
        }

        // 4. Widget en pantalla
        r = row(widgets > 0 ? OK : WARN, R.string.widget_title, 0);
        ((TextView) r.findViewById(R.id.status)).setText(widgets > 0
                ? getString(R.string.widget_ok, widgets) : getString(R.string.widget_bad));
        if (widgets == 0) {
            details(r, R.string.widget_symptom, R.string.widget_fix);
            if (awm.isRequestPinAppWidgetSupported()) {
                button(r, R.string.widget_add, true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        awm.requestPinAppWidget(new ComponentName(SetupActivity.this, DebugWidget.class), null, null);
                    }
                });
            }
        }
    }

    private void renderFaq() {
        LinearLayout faq = findViewById(R.id.faq);
        String[] symptoms = getResources().getStringArray(R.array.faq_symptoms);
        String[] fixes = getResources().getStringArray(R.array.faq_fixes);
        for (int i = 0; i < symptoms.length; i++) {
            if (i > 0) {
                View d = new View(this);
                d.setBackgroundColor(getColor(R.color.ui_stroke));
                faq.addView(d, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));
            }
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setPadding(0, dp(14), 0, dp(14));
            TextView s = new TextView(this);
            s.setText(symptoms[i]);
            s.setTextSize(15);
            s.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            s.setTextColor(getColor(R.color.ui_text));
            TextView f = new TextView(this);
            f.setText(fixes[i]);
            f.setTextSize(14);
            f.setLineSpacing(0, 1.2f);
            f.setTextColor(getColor(R.color.ui_text2));
            f.setPadding(0, dp(4), 0, 0);
            item.addView(s);
            item.addView(f);
            faq.addView(item);
        }
    }

    // status: OK/WARN/ERR; statusText 0 = lo pone quien llama.
    private View row(int status, int title, int statusText) {
        View r = LayoutInflater.from(this).inflate(R.layout.check_row, checks, false);
        TextView badge = r.findViewById(R.id.badge);
        int color = status == OK ? R.color.ok : status == WARN ? R.color.warn : R.color.err;
        badge.setText(status == OK ? "✓" : status == WARN ? "!" : "✕");
        badge.setTextColor(getColor(color));
        badge.setBackgroundResource(status == OK ? R.drawable.badge_ok : status == WARN ? R.drawable.badge_warn : R.drawable.badge_err);
        badge.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ((TextView) r.findViewById(R.id.title)).setText(title);
        TextView st = r.findViewById(R.id.status);
        st.setTextColor(getColor(color));
        if (statusText != 0) st.setText(statusText);
        checks.addView(r);
        return r;
    }

    private void details(View row, int symptom, int fix) {
        row.findViewById(R.id.details).setVisibility(View.VISIBLE);
        ((TextView) row.findViewById(R.id.symptom)).setText(symptom);
        ((TextView) row.findViewById(R.id.fix)).setText(fix);
    }

    private void code(View row, String cmd) {
        TextView t = new TextView(this);
        t.setText(cmd);
        t.setTypeface(Typeface.MONOSPACE);
        t.setTextSize(12);
        t.setLineSpacing(0, 1.2f);
        t.setTextIsSelectable(true);
        t.setTextColor(getColor(R.color.ui_text));
        t.setBackgroundResource(R.drawable.code_bg);
        t.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(10);
        ((LinearLayout) row.findViewById(R.id.extra)).addView(t, lp);
    }

    private void button(View row, int label, boolean primary, View.OnClickListener l) {
        Button b = new Button(this, null, 0, primary ? R.style.Btn_Primary : R.style.Btn_Tonal);
        b.setText(label);
        b.setOnClickListener(l);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(12);
        ((LinearLayout) row.findViewById(R.id.extra)).addView(b, lp);
    }

    private View.OnClickListener open(final Intent i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(SetupActivity.this, R.string.not_available, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
