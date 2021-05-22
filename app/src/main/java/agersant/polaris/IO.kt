package agersant.polaris

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json

object IO {
    @OptIn(ExperimentalSerializationApi::class)
    val cbor = Cbor {
        ignoreUnknownKeys = true
    }

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
}
