package agersant.polaris

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class CollectionItemSerialization {
    @Test
    fun songJson() {
        println("songJson")
        val song = Song(
            path = "Music/Shinedown/Threat to Survival/02 - Shinedown - Cut the Cord.m4a",
            artist = "Shinedown",
            title = "Cut the Cord",
            artwork = "Music/Shinedown/Threat to Survival/cover.jpg",
            album = "Threat to Survival",
            albumArtist = "Shinedown",
            composer = null,
            lyricist = null,
            genre = "Rock",
            label = null,
            trackNumber = 2,
            discNumber = -1,
            duration = 224,
            year = 2015,
        )

        val json = Json

        val string = json.encodeToString(CollectionItem.Serializer, song)
        val deserializedSong = json.decodeFromString(CollectionItem.Serializer, string)

        Assert.assertTrue(deserializedSong is Song)
        Assert.assertEquals(deserializedSong, song)
    }

    @Test
    fun dirJson() {
        println("dirJson")
        val dir = Directory(
            path = "Music/Shinedown/Threat to Survival",
            artist = "Shinedown",
            artwork = "Music/Shinedown/Threat to Survival/cover.jpg",
            album = "Threat to Survival",
            year = 2015,
        )

        val json = Json

        val string = json.encodeToString(CollectionItem.Serializer, dir)
        val deserializedDir = json.decodeFromString(CollectionItem.Serializer, string)

        Assert.assertTrue(deserializedDir is Directory)
        Assert.assertEquals(deserializedDir, dir)
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun songCbor() {
        println("songCbor")
        val song = Song(
            path = "Music/Shinedown/Threat to Survival/02 - Shinedown - Cut the Cord.m4a",
            artist = "Shinedown",
            title = "Cut the Cord",
            artwork = "Music/Shinedown/Threat to Survival/cover.jpg",
            album = "Threat to Survival",
            albumArtist = "Shinedown",
            composer = null,
            lyricist = null,
            genre = "Rock",
            label = null,
            trackNumber = 2,
            discNumber = -1,
            duration = 224,
            year = 2015,
        )

        val cbor = Cbor

        val bytes = cbor.encodeToByteArray(CollectionItem.Serializer, song)
        val deserializedSong = cbor.decodeFromByteArray(CollectionItem.Serializer, bytes)

        Assert.assertTrue(deserializedSong is Song)
        Assert.assertEquals(deserializedSong, song)
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun dirCbor() {
        println("dirCbor")
        val dir = Directory(
            path = "Music/Shinedown/Threat to Survival",
            artist = "Shinedown",
            artwork = "Music/Shinedown/Threat to Survival/cover.jpg",
            album = "Threat to Survival",
            year = 2015,
        )

        val cbor = Cbor

        val bytes = cbor.encodeToByteArray(CollectionItem.Serializer, dir)
        val deserializedDir = cbor.decodeFromByteArray(CollectionItem.Serializer, bytes)

        Assert.assertTrue(deserializedDir is Directory)
        Assert.assertEquals(deserializedDir, dir)
    }
}
