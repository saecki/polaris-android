plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("name.remal.check-dependency-updates") version "1.3.1"
}

android {
    compileSdkVersion(30)
    ndkVersion = "22.1.7171670"

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)

        applicationId = "agersant.polaris"
        versionName = "0.0"
        versionCode = 1
    }
    signingConfigs {
        create("release") {
            storeFile = File(System.getenv("SIGNING_KEYSTORE_PATH").orEmpty())
            storePassword = System.getenv("SIGNING_KEYSTORE_PASSWORD").orEmpty()
            keyAlias = System.getenv("SIGNING_KEY_ALIAS").orEmpty()
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD").orEmpty()
        }
    }
    buildTypes {
        getByName("release") {
            debuggable(false)
            minifyEnabled(false)

            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs["release"]
        }
        getByName("debug") {
            debuggable(true)
        }
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Core
    implementation(Deps.Kotlin.stdlib)
    implementation(Deps.Androidx.coreKtx)
    implementation(Deps.Androidx.appcompat)
    implementation(Deps.material)

    // Navigation
    implementation(Deps.Nav.fragmentKtx)
    implementation(Deps.Nav.uiKtx)

    // Preference
    implementation(Deps.Androidx.preferenceKtx)

    // Media session
    implementation(Deps.Androidx.media)

    // Media player
    implementation(Deps.ExoPlayer.core)
    implementation(Deps.ExoPlayer.flacExtension) { isTransitive = false }

    // Rest client
    implementation(Deps.Kotlinx.Serialization.core)
    implementation(Deps.Kotlinx.Serialization.json)
    implementation(Deps.KtorClient.serialization)
    implementation(Deps.KtorClient.core)
    implementation(Deps.KtorClient.okhttp)

    implementation(Deps.gson)
    implementation(Deps.okhttp)

    // Swipe refresh layout
    implementation(Deps.swipyRefresh)
}

task("printVersionCode") {
    doLast {
        println(android.defaultConfig.versionCode)
    }
}

task("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}
