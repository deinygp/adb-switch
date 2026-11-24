# Google Play listing / Ficha de Google Play

Limits: title 30 · short description 80 · full description 4000.

## en-US

**Title** (28)
ADB Switch: USB Debug Toggle

**Short description** (69)
Turn USB debugging on and off with one tap from a home-screen widget.

**Full description**

ADB Switch puts a USB debugging switch on your home screen. One tap turns it on, another turns it off — no more digging through Settings → System → Developer options.

FEATURES
• Segmented switch widget, 1 cell tall, resizable from 1 to 4+ cells wide; the content adapts to the width.
• Tap anywhere on the widget to toggle.
• Light and dark theme, following your system.
• Built-in setup guide that checks everything the widget needs and shows the symptom and the fix for each problem.
• No background services. The widget only does something when you tap it.

ONE-TIME SETUP (REQUIRED)
Android only lets apps change USB debugging with a special permission that must be granted from a computer, once:

adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS

The app shows this command with a copy button and re-checks automatically. Grant it again if you reinstall the app.

REQUIREMENTS
• Android 12 or newer.
• Developer options enabled.
• A computer with adb for the one-time permission.

GOOD TO KNOW
• Turning USB debugging off closes any active adb connection — that is expected.
• If wireless debugging is on, the adb service keeps running and the widget shows ON.
• On Android 17 apps can no longer read the debugging setting directly; ADB Switch reads the state of the adb service instead.

ADS
The setup screen shows small ads. The widget never shows ads.

OPEN SOURCE
Source code (MIT): github.com/deinygp/adb-switch

## es-419

**Título** (26)
ADB Switch: depuración USB

**Descripción breve** (66)
Activa y desactiva la depuración USB con un toque desde un widget.

**Descripción completa**

ADB Switch pone un interruptor de depuración USB en tu pantalla de inicio. Un toque la activa, otro la desactiva: ya no tienes que entrar a Ajustes → Sistema → Opciones de desarrollador.

FUNCIONES
• Widget tipo interruptor segmentado de 1 celda de alto, ajustable de 1 a 4+ celdas de ancho; el contenido se adapta al ancho.
• Se toca en cualquier punto del widget.
• Tema claro y oscuro según tu sistema.
• Guía de configuración integrada: revisa todo lo que el widget necesita y muestra el síntoma y la solución de cada problema.
• Sin servicios en segundo plano. El widget solo actúa cuando lo tocas.

CONFIGURACIÓN ÚNICA (OBLIGATORIA)
Android solo permite que una app cambie la depuración USB con un permiso especial que se otorga desde un computador, una sola vez:

adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS

La app muestra este comando con un botón para copiarlo y vuelve a revisar sola. Otórgalo de nuevo si reinstalas la app.

REQUISITOS
• Android 12 o superior.
• Opciones de desarrollador activadas.
• Un computador con adb para el permiso inicial.

BUENO SABER
• Al apagar la depuración USB se corta cualquier conexión adb activa: es lo esperado.
• Si la depuración inalámbrica está activa, el servicio adb sigue corriendo y el widget muestra ON.
• En Android 17 las apps ya no pueden leer directamente el ajuste de depuración; ADB Switch lee el estado del servicio adb.

PUBLICIDAD
La pantalla de configuración muestra anuncios pequeños. El widget nunca muestra anuncios.

CÓDIGO ABIERTO
Código fuente (MIT): github.com/deinygp/adb-switch

## Store settings / Configuración

- Category / Categoría: Tools / Herramientas
- Contains ads / Contiene anuncios: Yes / Sí
- Privacy policy / Política de privacidad: https://deinygp.github.io/adb-switch/privacy.html
- Contact / Contacto: GitHub issues (email required by Play Console: set by the owner)
- App access / Acceso a la app: no login; full functionality needs the adb permission above (explain in "App access" instructions)
- Target audience / Público objetivo: 18+ (developer tool)
- Data safety / Seguridad de los datos: collected by AdMob SDK — Device or other IDs (advertising ID), App activity (ad interactions), App info and performance (diagnostics), approximate location (from IP). Purpose: Advertising, Analytics, Fraud prevention. Shared with Google. Encrypted in transit: yes. Deletion request: not applicable (app stores no data).
