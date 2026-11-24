// Proyecto de un solo módulo: la raíz es la app.
plugins {
    id("com.android.application") version "9.1.0"
}

// Llave de subida a Google Play: vive fuera del repo, en ~/.gradle/gradle.properties
// (ADBSWITCH_KEYSTORE, ADBSWITCH_KEY_ALIAS, ADBSWITCH_KEY_PASSWORD). Sin ella, release queda sin firmar.
val keystore = providers.gradleProperty("ADBSWITCH_KEYSTORE").orNull

android {
    namespace = "com.deinygp.adbswitch"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.deinygp.adbswitch"
        minSdk = 31
        targetSdk = 36
        versionCode = 5
        versionName = "1.0"
    }

    signingConfigs {
        if (keystore != null) {
            create("release") {
                storeFile = file(keystore)
                storePassword = providers.gradleProperty("ADBSWITCH_KEY_PASSWORD").get()
                keyAlias = providers.gradleProperty("ADBSWITCH_KEY_ALIAS").get()
                keyPassword = providers.gradleProperty("ADBSWITCH_KEY_PASSWORD").get()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            if (keystore != null) signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
}
