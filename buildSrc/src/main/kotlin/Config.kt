object Secrets {
    val signingKeystorePath: String = System.getenv("SIGNING_KEYSTORE_PATH") ?: ""
    val signingKeystorePassword: String = System.getenv("SIGNING_KEYSTORE_PASSWORD") ?: ""
    val signingKeyAlias: String = System.getenv("SIGNING_KEY_ALIAS") ?: ""
    val signingKeyPassword: String = System.getenv("SIGNING_KEY_PASSWORD") ?: ""
}

object Config {
    val versionCode: Int = System.getenv("VERSION_CODE")?.toInt() ?: 1
}

object Versions {
    const val androidGradle = "4.1.1"
    const val kotlin = "1.4.21"
    const val navigation = "2.3.2"

    const val polaris = "0.8.5"
}

