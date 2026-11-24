#!/usr/bin/env bash
# Compila, instala y otorga permiso. Uso: ./build.sh
set -euo pipefail
cd "$(dirname "$0")"
SDK="${ANDROID_HOME:-$LOCALAPPDATA/Android/Sdk}"
BT="$SDK/build-tools/36.0.0"
JAR="$SDK/platforms/android-36/android.jar"

rm -rf out && mkdir -p out/gen out/classes
"$BT/aapt2" compile --dir res -o out/res.zip
"$BT/aapt2" link -I "$JAR" --manifest AndroidManifest.xml -o out/base.apk --java out/gen out/res.zip \
  --min-sdk-version 31 --target-sdk-version 36 --version-code 3 --version-name 3.0
javac -nowarn -source 8 -target 8 -bootclasspath "$JAR" -d out/classes $(find src out/gen -name '*.java')
"$BT/d8.bat" --lib "$JAR" --min-api 31 --output out $(find out/classes -name '*.class')
(cd out && "$BT/aapt" add base.apk classes.dex >/dev/null)
"$BT/zipalign" -f 4 out/base.apk out/aligned.apk
"$BT/apksigner.bat" sign --ks ~/.android/debug.keystore --ks-pass pass:android --out adb-switch.apk out/aligned.apk

adb install -r adb-switch.apk
adb shell pm grant com.deinygp.adbswitch android.permission.WRITE_SECURE_SETTINGS
echo "OK: agrega el widget 'ADB Switch' en la pantalla de inicio."
