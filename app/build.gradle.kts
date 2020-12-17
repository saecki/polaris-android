plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(29)
    ndkVersion = "21.3.6528147"

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(29)

        applicationId = "agersant.polarisnew"
        versionCode = Config.versionCode
        versionName = Versions.polaris
    }
    signingConfigs {
        create("release") {
            storeFile = File(Secrets.signingKeystorePath)
            storePassword = Secrets.signingKeystorePassword
            keyAlias = Secrets.signingKeyAlias
            keyPassword = Secrets.signingKeyPassword
        }
    }
    buildTypes {
        getByName("release") {
            debuggable(false)
            minifyEnabled(false)

            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
            signingConfig = signingConfigs["release"]
        }
        getByName("debug") {
            debuggable(true)
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // TODO: clean up if possible
    implementation(fileTree(mapOf(Pair("dir", "libs"), Pair("include", "*.jar"))))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")

    // UI
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.navigation}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")

    // Preferences
    implementation("androidx.preference:preference-ktx:1.1.1")

    // Media session
    implementation("androidx.media2:media2-session:1.1.1")
    implementation("androidx.media2:media2-widget:1.1.1")

    // Media player
    implementation("com.github.PaulWoitaschek.ExoPlayer-Extensions-Autobuild:flac:2.9.6")
    implementation("com.google.android.exoplayer:exoplayer-core:2.11.8")

    // Rest client
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.create("printVersionName") {
    doLast {
        println(Versions.polaris)
    }
}
