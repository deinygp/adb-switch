#!/usr/bin/env bash
# Compila (debug), instala en el dispositivo conectado y otorga el permiso. Uso: ./build.sh
# Para Google Play: ./gradlew bundleRelease  ->  build/outputs/bundle/release/adb-switch-release.aab
set -euo pipefail
cd "$(dirname "$0")"
./gradlew -q installDebug
adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS
echo "OK: agrega el widget 'ADB Switch' en la pantalla de inicio."
