object Deps {

    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-beta02"

    object Kotlin {
        const val version = "1.5.0"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
    }

    object Kotlinx {
        object Coroutines {
            const val version = "1.5.0"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        }

        object Serialization {
            const val version = "1.2.1"
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:$version"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
            const val cbor = "org.jetbrains.kotlinx:kotlinx-serialization-cbor:$version"
        }
    }

    object Androidx {
        object Media2 {
            const val version = "1.1.3"
            const val common = "androidx.media2:media2-common:$version"
            const val session = "androidx.media2:media2-session:$version"
            const val player = "androidx.media2:media2-player:$version"
        }

        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val coreKtx = "androidx.core:core-ktx:1.5.0"
        const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"
        const val media = "androidx.media:media:1.3.1"
    }

    object Nav {
        const val version = "2.4.0-alpha01"
        const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
    }

    object ExoPlayer {
        const val version = "2.13.3"
        const val core = "com.google.android.exoplayer:exoplayer-core:$version"
        const val flacExtension = "com.github.Saecki.ExoPlayer-Extensions:extension-flac:$version"
    }

    object KtorClient {
        const val version = "1.5.4"
        const val core = "io.ktor:ktor-client-core:$version"
        const val okhttp = "io.ktor:ktor-client-okhttp:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
    }

    const val material = "com.google.android.material:material:1.3.0"

    const val gson = "com.google.code.gson:gson:2.8.6"
    const val okhttp = "com.squareup.okhttp3:okhttp:4.9.1"

    const val swipyRefresh = "com.github.orangegangsters:swipy:1.2.3@aar"

    const val junit = "junit:junit:4.13.2"
}
