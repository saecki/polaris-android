package agersant.polaris

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.io.Serializable
import java.lang.reflect.Type

data class Playlist(val name: String): Serializable {

    object Deserializer : JsonDeserializer<Playlist> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Playlist {
            val name = json!!.asJsonObject.get("name").asString
            return Playlist(name)
        }
    }
}
