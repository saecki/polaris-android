package agersant.polaris

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = CollectionItem.Serializer::class)
sealed class CollectionItem private constructor(
    val isDirectory: Boolean,
    val path: String,
    val artist: String? = null,
    val title: String? = null,
    val artwork: String? = null,
    val album: String? = null,
    val albumArtist: String? = null,
    val composer: String? = null,
    val lyricist: String? = null,
    val genre: String? = null,
    val label: String? = null,
    val trackNumber: Int = -1,
    val discNumber: Int = -1,
    val duration: Int = -1,
    val year: Int = -1,
) : Cloneable {

    val name: String
        get() {
            val chunks = path.split(Regex("[/\\\\]")).filter(String::isNotEmpty)
            return chunks.last()
        }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): CollectionItem {
        return super.clone() as CollectionItem
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        when (other) {
            is Song -> if (this is Song) {
                return this.isDirectory == other.isDirectory
                    && this.path == other.path
                    && this.artist == other.artist
                    && this.title == other.title
                    && this.artwork == other.artwork
                    && this.album == other.album
                    && this.albumArtist == other.albumArtist
                    && this.composer == other.composer
                    && this.lyricist == other.lyricist
                    && this.genre == other.genre
                    && this.label == other.label
                    && this.trackNumber == other.trackNumber
                    && this.discNumber == other.discNumber
                    && this.duration == other.duration
                    && this.year == other.year
            }
            is Directory -> if (this is Directory) {
                return this.isDirectory == other.isDirectory
                    && this.path == other.path
                    && this.artist == other.artist
                    && this.artwork == other.artwork
                    && this.album == other.album
                    && this.year == other.year
            }
        }

        return false
    }

    @Serializable(with = Directory.Serializer::class)
    class Directory(
        path: String,
        artist: String? = null,
        artwork: String? = null,
        album: String? = null,
        year: Int = -1,
    ) : CollectionItem(
        isDirectory = true,
        path = path,
        artist = artist,
        artwork = artwork,
        album = album,
        year = year,
    ) {

        object Serializer : KSerializer<Directory> {
            override val descriptor: SerialDescriptor
                get() = buildClassSerialDescriptor("Directory") {
                    element<String>("path")
                    element<String>("artist")
                    element<String>("artwork")
                    element<String>("album")
                    element<Int>("year")
                }

            override fun deserialize(decoder: Decoder): Directory = decoder.decodeStructure(descriptor) {
                var path: String? = null
                var artist: String? = null
                var artwork: String? = null
                var album: String? = null
                var year: Int = -1

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> path = decodeStringElement(descriptor, index)
                        1 -> artist = decodeStringElement(descriptor, index)
                        2 -> artwork = decodeStringElement(descriptor, index)
                        3 -> album = decodeStringElement(descriptor, index)
                        4 -> year = decodeIntElement(descriptor, index)
                        CompositeDecoder.DECODE_DONE -> break
                    }
                }

                path ?: error("Missing field 'path'")

                return Directory(
                    path = path,
                    artist = artist,
                    artwork = artwork,
                    album = album,
                    year = year,
                )
            }

            override fun serialize(encoder: Encoder, value: Directory) = encoder.encodeStructure(descriptor) {
                encodeOptionalStringElement(descriptor, 0, value.path)
                encodeOptionalStringElement(descriptor, 1, value.artist)
                encodeOptionalStringElement(descriptor, 2, value.artwork)
                encodeOptionalStringElement(descriptor, 3, value.album)
                encodeOptionalIntElement(descriptor, 4, value.year)
            }
        }
    }

    @Serializable(with = Song.Serializer::class)
    class Song(
        path: String,
        artist: String? = null,
        title: String? = null,
        artwork: String? = null,
        album: String? = null,
        albumArtist: String? = null,
        composer: String? = null,
        lyricist: String? = null,
        genre: String? = null,
        label: String? = null,
        trackNumber: Int = -1,
        discNumber: Int = -1,
        duration: Int = -1,
        year: Int = -1,
    ) : CollectionItem(
        isDirectory = false,
        path = path,
        artist = artist,
        title = title,
        artwork = artwork,
        album = album,
        albumArtist = albumArtist,
        composer = composer,
        lyricist = lyricist,
        genre = genre,
        label = label,
        trackNumber = trackNumber,
        discNumber = discNumber,
        duration = duration,
        year = year,
    ) {

        object Serializer : KSerializer<Song> {
            override val descriptor: SerialDescriptor
                get() = buildClassSerialDescriptor("Song") {
                    element<String>("path")
                    element<String>("artist")
                    element<String>("title")
                    element<String>("artwork")
                    element<String>("album")
                    element<String>("album_artist")
                    element<String>("composer")
                    element<String>("lyricist")
                    element<String>("genre")
                    element<String>("label")
                    element<Int>("track_number")
                    element<Int>("disc_number")
                    element<Int>("duration")
                    element<Int>("year")
                }

            override fun deserialize(decoder: Decoder): Song = decoder.decodeStructure(descriptor) {
                var path: String? = null
                var artist: String? = null
                var title: String? = null
                var artwork: String? = null
                var album: String? = null
                var albumArtist: String? = null
                var composer: String? = null
                var lyricist: String? = null
                var genre: String? = null
                var label: String? = null
                var trackNumber: Int = -1
                var discNumber: Int = -1
                var duration: Int = -1
                var year: Int = -1

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> path = decodeStringElement(descriptor, index)
                        1 -> artist = decodeStringElement(descriptor, index)
                        2 -> title = decodeStringElement(descriptor, index)
                        3 -> artwork = decodeStringElement(descriptor, index)
                        4 -> album = decodeStringElement(descriptor, index)
                        5 -> albumArtist = decodeStringElement(descriptor, index)
                        6 -> composer = decodeStringElement(descriptor, index)
                        7 -> lyricist = decodeStringElement(descriptor, index)
                        8 -> genre = decodeStringElement(descriptor, index)
                        9 -> label = decodeStringElement(descriptor, index)
                        10 -> trackNumber = decodeIntElement(descriptor, index)
                        11 -> discNumber = decodeIntElement(descriptor, index)
                        12 -> duration = decodeIntElement(descriptor, index)
                        13 -> year = decodeIntElement(descriptor, index)
                        CompositeDecoder.DECODE_DONE -> break
                    }
                }

                path ?: error("Missing field 'path'")

                return Song(
                    path = path,
                    artist = artist,
                    title = title,
                    artwork = artwork,
                    album = album,
                    albumArtist = albumArtist,
                    composer = composer,
                    lyricist = lyricist,
                    genre = genre,
                    label = label,
                    trackNumber = trackNumber,
                    discNumber = discNumber,
                    duration = duration,
                    year = year,
                )
            }

            override fun serialize(encoder: Encoder, value: Song) = encoder.encodeStructure(descriptor) {
                encodeOptionalStringElement(descriptor, 0, value.path)
                encodeOptionalStringElement(descriptor, 1, value.artist)
                encodeOptionalStringElement(descriptor, 2, value.title)
                encodeOptionalStringElement(descriptor, 3, value.artwork)
                encodeOptionalStringElement(descriptor, 4, value.album)
                encodeOptionalStringElement(descriptor, 5, value.albumArtist)
                encodeOptionalStringElement(descriptor, 6, value.composer)
                encodeOptionalStringElement(descriptor, 7, value.lyricist)
                encodeOptionalStringElement(descriptor, 8, value.genre)
                encodeOptionalStringElement(descriptor, 9, value.label)
                encodeOptionalIntElement(descriptor, 10, value.trackNumber)
                encodeOptionalIntElement(descriptor, 11, value.discNumber)
                encodeOptionalIntElement(descriptor, 12, value.duration)
                encodeOptionalIntElement(descriptor, 13, value.year)
            }
        }
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
                    0 -> return decodeSerializableElement(descriptor, index, Song.Serializer)
                    1 -> return decodeSerializableElement(descriptor, index, Directory.Serializer)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            error("Missing field 'Song' or 'Directory'")
        }

        override fun serialize(encoder: Encoder, value: CollectionItem) = encoder.encodeStructure(descriptor) {
            when (value) {
                is Song -> encodeSerializableElement(descriptor, 0, Song.Serializer, value)
                is Directory -> encodeSerializableElement(descriptor, 1, Directory.Serializer, value)
            }
        }
    }
}

fun CompositeEncoder.encodeOptionalStringElement(descriptor: SerialDescriptor, index: Int, value: String?) {
    if (value != null) encodeStringElement(descriptor, index, value)
}

fun CompositeEncoder.encodeOptionalIntElement(descriptor: SerialDescriptor, index: Int, value: Int?) {
    if (value != null) encodeIntElement(descriptor, index, value)
}
