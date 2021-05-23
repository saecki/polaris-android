package agersant.polaris

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = CollectionItem.Serializer::class)
sealed class CollectionItem {
    abstract val path: String
    abstract val artist: String?
    abstract val artwork: String?
    abstract val album: String?
    abstract val year: Int

    val isDirectory: Boolean
        get() = this is Directory

    val name: String
        get() {
            val chunks = path.split(Regex("[/\\\\]")).filter(String::isNotEmpty)
            return chunks.last()
        }

    object Serializer : KSerializer<CollectionItem> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("CollectionItem") {
                element<Song>("Song", isOptional = true)
                element<Directory>("Directory", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): CollectionItem = decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> return decodeSerializableElement(descriptor, index, Song.serializer())
                    1 -> return decodeSerializableElement(descriptor, index, Directory.serializer())
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            error("Missing field 'Song' or 'Directory'")
        }

        override fun serialize(encoder: Encoder, value: CollectionItem) = encoder.encodeStructure(descriptor) {
            when (value) {
                is Song -> encodeSerializableElement(descriptor, 0, Song.serializer(), value)
                is Directory -> encodeSerializableElement(descriptor, 1, Directory.serializer(), value)
            }
        }
    }
}

@Serializable
data class Song(
    @SerialName("path") override val path: String,
    @SerialName("artist") override val artist: String? = null,
    @SerialName("artwork") override val artwork: String? = null,
    @SerialName("album") override val album: String? = null,
    @SerialName("year") override val year: Int = -1,
    @SerialName("title") val title: String? = null,
    @SerialName("album_artist") val albumArtist: String? = null,
    @SerialName("composer") val composer: String? = null,
    @SerialName("lyricist") val lyricist: String? = null,
    @SerialName("genre") val genre: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("track_number") val trackNumber: Int = -1,
    @SerialName("disc_number") val discNumber: Int = -1,
    @SerialName("duration") val duration: Int = -1,
) : CollectionItem()

@Serializable
data class Directory(
    @SerialName("path") override val path: String,
    @SerialName("artist") override val artist: String? = null,
    @SerialName("artwork") override val artwork: String? = null,
    @SerialName("album") override val album: String? = null,
    @SerialName("year") override val year: Int = -1,
    @SerialName("date_added") val dateAdded: Int = -1,
) : CollectionItem()
