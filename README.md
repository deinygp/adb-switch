# ADB Switch

[English](#english) · [Español](#español)

---

## English

Android home-screen widget to turn **USB debugging** on and off with one tap.

- Segmented switch, 1 cell tall; width resizes from 1 to 4+ cells and the content adapts (icon only → "ADB" → "Depuración" → "Depuración USB").
- Tap anywhere on the widget.
- Light and dark mode, following the system theme.
- No activity, no background services, no network permission: a single `AppWidgetProvider`.

### Requirements

- Android 12 or newer (API 31).
- Developer options enabled on the phone.
- A computer with `adb` to install and grant the permission **once**.

### Install

The app needs `WRITE_SECURE_SETTINGS`, a permission that can only be granted through adb:

```sh
adb install adb-switch.apk
adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS
```

Then long-press the home screen → **Widgets** → **ADB Switch**.

> If you reinstall the app, grant the permission again.

### Build from source

No Gradle: `build.sh` builds with the Android SDK tools (`aapt2`, `javac`, `d8`, `apksigner`), installs on the connected device and grants the permission.

```sh
./build.sh
```

Requires `ANDROID_HOME` with `build-tools/36.0.0` and `platforms/android-36`, a JDK, and the debug keystore at `~/.android/debug.keystore`. On Windows it runs from Git Bash.

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
- Sin activity, sin servicios en segundo plano, sin permisos de red: un único `AppWidgetProvider`.

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

No usa Gradle: `build.sh` compila con las herramientas del Android SDK (`aapt2`, `javac`, `d8`, `apksigner`), instala en el dispositivo conectado y otorga el permiso.

```sh
./build.sh
```

Requiere `ANDROID_HOME` con `build-tools/36.0.0` y `platforms/android-36`, un JDK y la keystore de debug en `~/.android/debug.keystore`. En Windows funciona desde Git Bash.

### Notas

- **Android 17 oculta el estado de depuración a las apps**: `Settings.Global.ADB_ENABLED` y `development_settings_enabled` devuelven `0` a apps de terceros aunque estén activos. Por eso el estado se lee de la propiedad `init.svc.adbd` (si el demonio adb está corriendo); la escritura del ajuste sí funciona con el permiso otorgado.
- Si la depuración inalámbrica está activa, `adbd` sigue corriendo y el widget mostrará ON.
- Si cambias la depuración desde Ajustes, el widget se actualiza en el siguiente toque.
- Al apagar la depuración se corta cualquier conexión adb activa con el PC; es lo esperado.

### Licencia

[MIT](LICENSE)
