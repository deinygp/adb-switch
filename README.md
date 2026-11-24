# ADB Switch

[English](#english) · [Español](#español)

---

## English

Android home-screen widget to turn **USB debugging** on and off with one tap.

- Segmented switch, 1 cell tall; width resizes from 1 to 4+ cells and the content adapts (icon only → "ADB" → "Depuración" → "Depuración USB").
- Tap anywhere on the widget.
- Light and dark mode, following the system theme.
- Built-in setup guide (open the app): checks the permission, whether the state can be read, developer options and whether the widget is on the home screen, and shows the symptom and fix for each problem, plus a troubleshooting list. Without the permission, tapping the widget opens this guide.
- Small ads on the setup screen only (AdMob, with GDPR consent); the widget never shows ads. No background services. English and Spanish UI.

### Requirements

- Android 12 or newer (API 31).
- Developer options enabled on the phone.
- A computer with `adb` to install and grant the permission **once**.

### Install

The app needs `WRITE_SECURE_SETTINGS`, a permission that can only be granted through adb:

```sh
# install from Google Play, or: adb install adb-switch.apk
adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS
```

Then long-press the home screen → **Widgets** → **ADB Switch**.

> If you reinstall the app, grant the permission again.

### Build from source

Gradle project (Android Gradle Plugin 9, JDK 17+):

```sh
./build.sh                 # debug build, install on the connected device and grant the permission
./gradlew bundleRelease    # Play bundle: build/outputs/bundle/release/adb-switch-release.aab
```

Release signing reads `ADBSWITCH_KEYSTORE`, `ADBSWITCH_KEY_ALIAS` and `ADBSWITCH_KEY_PASSWORD` from `~/.gradle/gradle.properties` (never from the repo). Ad unit IDs live in `src/main/res/values/ads.xml` (Google test IDs by default).

### Notes

- **Android 17 hides the debugging state from apps**: `Settings.Global.ADB_ENABLED` and `development_settings_enabled` return `0` to third-party apps even when enabled. The state is therefore read from the `init.svc.adbd` property (whether the adb daemon is running); writing the setting does work with the granted permission.
- If wireless debugging is on, `adbd` keeps running and the widget shows ON.
- If you change debugging from Settings, the widget updates on the next tap.
- Turning debugging off drops any active adb connection to your computer; that is expected.

### License

[MIT](LICENSE)

---

## Español

Widget de Android para activar y desactivar la **depuración USB** con un toque desde la pantalla de inicio.

- Switch segmentado de 1 celda de alto; el ancho se ajusta de 1 a 4+ celdas y el contenido se adapta (solo ícono → «ADB» → «Depuración» → «Depuración USB»).
- Se toca en cualquier punto del widget.
- Modo claro y oscuro según el tema del sistema.
- Guía de configuración integrada (al abrir la app): revisa el permiso, si se puede leer el estado, las Opciones de desarrollador y si el widget está en pantalla, y muestra síntoma y solución para cada problema, además de una lista de problemas frecuentes. Sin el permiso, tocar el widget abre esta guía.
- Anuncios pequeños solo en la pantalla de configuración (AdMob, con consentimiento GDPR); el widget nunca muestra anuncios. Sin servicios en segundo plano. Interfaz en inglés y español.

### Requisitos

- Android 12 o superior (API 31).
- Opciones de desarrollador activadas en el teléfono.
- Un PC con `adb` para instalar y otorgar el permiso **una sola vez**.

### Instalación

La app necesita `WRITE_SECURE_SETTINGS`, un permiso que solo se puede otorgar por adb:

```sh
adb install adb-switch.apk
adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS
```

Luego mantén presionada la pantalla de inicio → **Widgets** → **ADB Switch**.

> Si reinstalas la app, vuelve a otorgar el permiso.

### Compilar desde el código

Proyecto Gradle (Android Gradle Plugin 9, JDK 17+):

```sh
./build.sh                 # build debug, instala en el dispositivo conectado y otorga el permiso
./gradlew bundleRelease    # bundle para Play: build/outputs/bundle/release/adb-switch-release.aab
```

La firma de release lee `ADBSWITCH_KEYSTORE`, `ADBSWITCH_KEY_ALIAS` y `ADBSWITCH_KEY_PASSWORD` desde `~/.gradle/gradle.properties` (nunca del repo). Los IDs de anuncios están en `src/main/res/values/ads.xml` (IDs de prueba de Google por defecto).

### Notas

- **Android 17 oculta el estado de depuración a las apps**: `Settings.Global.ADB_ENABLED` y `development_settings_enabled` devuelven `0` a apps de terceros aunque estén activos. Por eso el estado se lee de la propiedad `init.svc.adbd` (si el demonio adb está corriendo); la escritura del ajuste sí funciona con el permiso otorgado.
- Si la depuración inalámbrica está activa, `adbd` sigue corriendo y el widget mostrará ON.
- Si cambias la depuración desde Ajustes, el widget se actualiza en el siguiente toque.
- Al apagar la depuración se corta cualquier conexión adb activa con el PC; es lo esperado.

### Licencia

[MIT](LICENSE)
