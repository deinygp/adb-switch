package com.deinygp.adbswitch;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.SizeF;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Map;

public class DebugWidget extends AppWidgetProvider {
    private static final String TOGGLE = "com.deinygp.adbswitch.TOGGLE";

    // Variantes por ancho mínimo (dp): el launcher elige la más grande que cabe.
    // Calibrado en Pixel 9: un 3×1 mide ~210 dp y ahí solo cabe "ADB".
    private static final float[] MIN_W = {40f, 160f, 235f, 280f};
    private static final int[] TITLE = {0, R.string.w_title_1, R.string.w_title_2, R.string.w_title_3};

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!TOGGLE.equals(intent.getAction())) {
            refresh(ctx);
            return;
        }
        boolean target = !isOn();
        try {
            Settings.Global.putInt(ctx.getContentResolver(), Settings.Global.ADB_ENABLED, target ? 1 : 0);
            render(ctx, target); // respuesta inmediata; adbd arranca/para con retraso
        } catch (SecurityException e) {
            // Normalmente no se llega aquí: sin permiso el widget abre la guía en vez de este broadcast.
            Toast.makeText(ctx, R.string.perm_title, Toast.LENGTH_LONG).show();
        }
        // Re-verifica el estado real cuando el sistema ya aplicó el cambio.
        final PendingResult pr = goAsync();
        final Context app = ctx.getApplicationContext();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh(app);
                pr.finish();
            }
        }, 2500);
        // ponytail: si cambias ADB desde Ajustes, el widget se ve desfasado hasta el próximo toque (o al abrir la app)
    }

    static boolean hasPermission(Context ctx) {
        return ctx.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }

    // Android 17 devuelve 0 en Settings.Global.ADB_ENABLED a apps de terceros (oculta las
    // Opciones de desarrollador); el estado real se infiere de si el demonio adbd corre.
    // ponytail: con depuración inalámbrica activa adbd también corre, y el widget mostrará ON
    static boolean isOn() {
        return "running".equals(prop("init.svc.adbd"));
    }

    static String prop(String k) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class).invoke(null, k);
        } catch (Throwable t) {
            return "";
        }
    }

    static void refresh(Context ctx) {
        render(ctx, isOn());
    }

    private static void render(Context ctx, boolean on) {
        boolean ready = hasPermission(ctx);
        // Sin permiso, tocar el widget abre la guía de configuración.
        PendingIntent pi = ready
                ? PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, DebugWidget.class).setAction(TOGGLE), PendingIntent.FLAG_IMMUTABLE)
                : PendingIntent.getActivity(ctx, 1, new Intent(ctx, SetupActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_IMMUTABLE);
        int status = !ready ? R.string.w_setup : on ? R.string.w_on : R.string.w_off;
        int desc = !ready ? R.string.w_cd_setup : on ? R.string.w_cd_on : R.string.w_cd_off;

        Map<SizeF, RemoteViews> sizes = new ArrayMap<>();
        for (int i = 0; i < MIN_W.length; i++) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget);
            boolean tiny = i == 0;
            // Mostrar un fantasma y ocultar el otro mueve el bloque (animateLayoutChanges).
            v.setViewVisibility(R.id.ghost_left, on ? View.VISIBLE : View.GONE);
            v.setViewVisibility(R.id.ghost_right, on ? View.GONE : View.VISIBLE);
            v.setTextViewText(R.id.ghost_left, tiny ? "" : "OFF");
            v.setTextViewText(R.id.ghost_right, tiny ? "" : "ON");
            v.setViewVisibility(R.id.texts, tiny ? View.GONE : View.VISIBLE);
            v.setTextViewText(R.id.title, tiny ? "" : ctx.getString(TITLE[i]));
            v.setTextViewText(R.id.status, ctx.getString(status));
            // Colores por recurso: se resuelven en el launcher, respetando modo claro/oscuro.
            v.setInt(R.id.ind, "setBackgroundResource", on ? R.drawable.ind_on : R.drawable.ind_off);
            v.setInt(R.id.icon, "setBackgroundResource", on ? R.drawable.circle_on : R.drawable.circle_off);
            v.setColorStateList(R.id.icon, "setImageTintList", on ? R.color.icon_on : R.color.icon_off);
            v.setColor(R.id.status, "setTextColor", on ? R.color.status_on : R.color.status_off);
            v.setContentDescription(R.id.root, ctx.getString(desc));
            v.setOnClickPendingIntent(R.id.root, pi);
            sizes.put(new SizeF(MIN_W[i], 40f), v);
        }
        AppWidgetManager.getInstance(ctx).updateAppWidget(new ComponentName(ctx, DebugWidget.class), new RemoteViews(sizes));
    }
}
