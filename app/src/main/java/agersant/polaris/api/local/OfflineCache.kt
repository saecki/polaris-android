package agersant.polaris.api.local

import agersant.polaris.CollectionItem
import agersant.polaris.IO
import agersant.polaris.PlaybackQueue
import agersant.polaris.PolarisApp
import agersant.polaris.PolarisPlayer
import agersant.polaris.R
import agersant.polaris.api.ThumbnailSize
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class OfflineCache(
    context: Context,
    private val playbackQueue: PlaybackQueue,
    private val player: PolarisPlayer
) {

    companion object {
        const val AUDIO_CACHED = "AUDIO_CACHED"
        const val AUDIO_REMOVED_FROM_CACHE = "AUDIO_REMOVED_FROM_CACHE"
        private const val ITEM_FILENAME = "__polaris__item"
        private const val AUDIO_FILENAME = "__polaris__audio"
        private const val ARTWORK_SMALL_FILENAME = "__polaris__artwork_small"
        private const val ARTWORK_LARGE_FILENAME = "__polaris__artwork_large"
        private const val ARTWORK_NATIVE_FILENAME = "__polaris__artwork_native"
        private const val META_FILENAME = "__polaris__meta"
        private const val FIRST_VERSION = 1
        private const val VERSION = 4
        private const val BUFFER_SIZE = 1024 * 64
    }

    private val dataSourceFactory = DefaultDataSourceFactory(context, "Polaris Local")
    private lateinit var root: File

    init {
        for (i in FIRST_VERSION..VERSION) {
            root = File(context.externalCacheDir, "collection")
            root = File(root, "v$i")
            if (i != VERSION) {
                deleteDirectory(root)
            }
        }
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val cacheSizeKey = run {
        val resources = context.resources
        resources.getString(R.string.pref_key_offline_cache_size)
    }
    private val cacheCapacity: Long
        get() {
            val cacheSizeString = preferences.getString(cacheSizeKey, "0")
            val cacheSize = cacheSizeString!!.toLong()
            return if (cacheSize < 0) {
                Long.MAX_VALUE
            } else cacheSize * 1024 * 1024
        }

    @Throws(IOException::class)
    private fun write(item: CollectionItem, storage: OutputStream) {
        @OptIn(ExperimentalSerializationApi::class)
        val bytes = IO.cbor.encodeToByteArray(CollectionItem.Serializer, item)
        storage.write(bytes)
    }

    @Throws(IOException::class)
    private fun write(audio: FileInputStream, storage: OutputStream) {
        val buffer = ByteArray(BUFFER_SIZE)
        var read: Int
        while (audio.read(buffer).also { read = it } > 0) {
            storage.write(buffer, 0, read)
        }
    }

    private fun write(image: Bitmap, storage: OutputStream) {
        image.compress(Bitmap.CompressFormat.PNG, 100, storage)
    }

    @Throws(IOException::class)
    private fun write(metadata: ItemCacheMetadata, storage: OutputStream) {
        @OptIn(ExperimentalSerializationApi::class)
        val bytes = IO.cbor.encodeToByteArray(ItemCacheMetadata.serializer(), metadata)
        storage.write(bytes)
    }

    private fun listDeletionCandidates(path: File): MutableList<DeletionCandidate> {
        val candidates = mutableListOf<DeletionCandidate>()

        val files = path.listFiles() ?: return candidates
        for (child in files) {
            val audio = File(child, AUDIO_FILENAME)
            if (audio.exists()) {

                var metadata = ItemCacheMetadata()
                metadata.lastUse = 0L
                val meta = File(child, META_FILENAME)
                if (meta.exists()) {
                    try {
                        metadata = readMetadata(meta)
                    } catch (e: IOException) {
                        println("Error reading file metadata for $child $e")
                    }
                }

                var item: CollectionItem? = null
                try {
                    item = readItem(child)
                } catch (e: Exception) {
                    println("Error reading collection item for $child $e")
                }

                val candidate = DeletionCandidate(child, metadata, item)
                candidates.add(candidate)
            } else if (child.isDirectory) {
                candidates.addAll(listDeletionCandidates(child))
            }
        }

        return candidates
    }

    private fun getCacheSize(file: File): Long {
        var size: Long = 0
        if (!file.exists()) {
            return 0
        }
        val files = file.listFiles()
        if (files != null) {
            for (child in files) {
                size += child.length()
                if (child.isDirectory) {
                    size += getCacheSize(child)
                }
            }
        }
        return size
    }

    private fun removeOldAudio(path: File, newItem: CollectionItem, bytesToSave: Long): Boolean {
        val candidates = listDeletionCandidates(path)

        candidates.sortWith { a: DeletionCandidate, b: DeletionCandidate ->
            when {
                a.item == null && b.item == null -> {
                    (a.metadata.lastUse - b.metadata.lastUse).toInt()
                }
                a.item == null -> -1
                b.item == null -> 1
                else -> -playbackQueue.comparePriorities(player.currentItem, a.item, b.item)
            }
        }

        var cleared: Long = 0
        for (candidate in candidates) {
            if (candidate.item != null) {
                if (playbackQueue.comparePriorities(
                        player.currentItem,
                        candidate.item,
                        newItem
                    ) <= 0
                ) {
                    continue
                }
            }
            val audio = File(candidate.cachePath, AUDIO_FILENAME)
            if (audio.exists()) {
                val size = audio.length()
                if (audio.delete()) {
                    println("Deleting $audio")
                    cleared += size
                }
                if (cleared >= bytesToSave) {
                    break
                }
            }
        }
        if (cleared > 0) {
            broadcast(AUDIO_REMOVED_FROM_CACHE)
        }
        return cleared >= bytesToSave
    }

    @Synchronized
    fun makeSpace(item: CollectionItem): Boolean {
        val cacheSize = getCacheSize(root)
        val cacheCapacity = cacheCapacity
        val overflow = cacheSize - cacheCapacity
        var success = true
        if (overflow > 0) {
            success = removeOldAudio(root, item, overflow)
            removeEmptyDirectories(root)
        }
        return success
    }

    private fun deleteDirectory(path: File) {
        if (!path.exists()) {
            return
        }
        val files = path.listFiles() ?: return
        for (child in files) {
            if (child.isDirectory) {
                deleteDirectory(child)
            } else {
                child.delete()
            }
        }
        path.delete()
    }

    private fun removeEmptyDirectories(path: File) {
        // TODO: Catastrophic complexity
        val files = path.listFiles() ?: return
        for (child in files) {
            if (child.isDirectory) {
                if (!containsAudio(child)) {
                    println("Deleting $child")
                    deleteDirectory(child)
                } else {
                    removeEmptyDirectories(child)
                }
            }
        }
    }

    @Synchronized
    fun putAudio(item: CollectionItem, audio: FileInputStream?) {
        makeSpace(item) // TODO we don't need this called so often. Every n minutes should do.
        val path = item.path
        try {
            FileOutputStream(createCacheFile(path, CacheDataType.ITEM)).use { itemOut ->
                write(item, itemOut)
            }
        } catch (e: IOException) {
            println("Error while caching item for local use: $e")
            return
        }
        if (audio != null) {
            try {
                FileOutputStream(createCacheFile(path, CacheDataType.AUDIO)).use { itemOut ->
                    write(audio, itemOut)
                    broadcast(AUDIO_CACHED)
                }
            } catch (e: IOException) {
                println("Error while caching audio for local use: $e")
                return
            }
        }
        if (!hasMetadata(path)) {
            saveMetadata(path, ItemCacheMetadata())
        }
        println("Saved audio to offline cache: $path")
    }

    @Synchronized
    fun putImage(item: CollectionItem, size: ThumbnailSize, image: Bitmap) {
        try {
            FileOutputStream(createCacheFile(item.path, CacheDataType.ITEM)).use { itemOut ->
                write(item, itemOut)
            }
        } catch (e: IOException) {
            println("Error while caching item for local use: $e")
        }
        val cacheDataType = getImageCacheDataType(size)
        try {
            FileOutputStream(createCacheFile(item.artwork!!, cacheDataType)).use { itemOut ->
                write(image, itemOut)
            }
        } catch (e: IOException) {
            println("Error while caching artwork for local use: $e")
        }
        println("Saved image to offline cache: ${item.artwork}")
    }

    private fun getCacheDir(virtualPath: String): File {
        val path = virtualPath.replace("\\", File.separator)
        return File(root, path)
    }

    private fun getCacheFile(virtualPath: String, type: CacheDataType): File {
        val file = getCacheDir(virtualPath)
        return when (type) {
            CacheDataType.ITEM -> File(file, ITEM_FILENAME)
            CacheDataType.AUDIO -> File(file, AUDIO_FILENAME)
            CacheDataType.ARTWORK_SMALL -> File(file, ARTWORK_SMALL_FILENAME)
            CacheDataType.ARTWORK_LARGE -> File(file, ARTWORK_LARGE_FILENAME)
            CacheDataType.ARTWORK_NATIVE -> File(file, ARTWORK_NATIVE_FILENAME)
            CacheDataType.META -> File(file, META_FILENAME)
        }
    }

    private fun getImageCacheDataType(size: ThumbnailSize): CacheDataType {
        return when (size) {
            ThumbnailSize.Large -> CacheDataType.ARTWORK_LARGE
            ThumbnailSize.Native -> CacheDataType.ARTWORK_NATIVE
            ThumbnailSize.Small -> CacheDataType.ARTWORK_SMALL
        }
    }

    @Throws(IOException::class)
    private fun createCacheFile(virtualPath: String, type: CacheDataType): File {
        val file = getCacheFile(virtualPath, type)
        val parent = file.parentFile!!
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw IOException("Could not create cache directory: $parent")
            }
        }
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw IOException("Could not create cache file: $file")
            }
        }
        return file
    }

    fun hasAudio(path: String): Boolean {
        val file = getCacheFile(path, CacheDataType.AUDIO)
        return file.exists()
    }

    fun hasImage(virtualPath: String, size: ThumbnailSize): Boolean {
        val cacheDataType = getImageCacheDataType(size)
        val file = getCacheFile(virtualPath, cacheDataType)
        return file.exists()
    }

    fun getAudio(virtualPath: String): MediaSource? {
        if (!hasAudio(virtualPath)) return null

        if (hasMetadata(virtualPath)) {
            val metadata = getMetadata(virtualPath)
            metadata.updateUse()
            saveMetadata(virtualPath, metadata)
        }
        val uri = Uri.fromFile(getCacheFile(virtualPath, CacheDataType.AUDIO))
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    fun getImage(virtualPath: String, size: ThumbnailSize): Bitmap? {
        if (!hasImage(virtualPath, size)) return null

        val cacheDataType = getImageCacheDataType(size)
        val file = getCacheFile(virtualPath, cacheDataType)
        return try {
            val fileInputStream = FileInputStream(file)
            BitmapFactory.decodeFileDescriptor(fileInputStream.fd)
        } catch (e: IOException) {
            println("Error loading image from disk $file: $e")
            null
        }
    }

    private fun saveMetadata(virtualPath: String, metadata: ItemCacheMetadata) {
        try {
            FileOutputStream(createCacheFile(virtualPath, CacheDataType.META)).use {
                write(metadata, it)
            }
        } catch (e: IOException) {
            println("Error while caching metadata for local use: $e")
        }
    }

    private fun hasMetadata(virtualPath: String): Boolean {
        val file = getCacheFile(virtualPath, CacheDataType.META)
        return file.exists()
    }

    @Throws(IOException::class)
    private fun readMetadata(file: File): ItemCacheMetadata {
        try {
            FileInputStream(file).use { fis ->
                @OptIn(ExperimentalSerializationApi::class)
                return IO.cbor.decodeFromByteArray(ItemCacheMetadata.serializer(), fis.readBytes())
            }
        } catch (e: SerializationException) {
            println("Error deserializing metadata file: $file")
            throw FileNotFoundException()
        }
    }

    @Throws(IOException::class)
    private fun getMetadata(virtualPath: String): ItemCacheMetadata {
        if (!hasMetadata(virtualPath)) {
            throw FileNotFoundException()
        }
        val file = getCacheFile(virtualPath, CacheDataType.META)
        return readMetadata(file)
    }

    fun browse(path: String): List<CollectionItem>? {
        val out = mutableListOf<CollectionItem>()
        val dir = getCacheDir(path)

        val files = dir.listFiles() ?: return out
        for (file in files) {
            try {
                if (!file.isDirectory) {
                    continue
                }
                if (isInternalFile(file)) {
                    continue
                }
                val item = readItem(file)
                if (item != null) {
                    if (item.isDirectory) {
                        if (!containsAudio(file)) {
                            continue
                        }
                    }
                    out.add(item)
                }
            } catch (e: IOException) {
                println("Error while reading offline cache: $e")
                return null
            } catch (e: ClassNotFoundException) {
                println("Error while reading offline cache: $e")
                return null
            }
        }
        return out
    }

    fun flatten(path: String): List<CollectionItem>? {
        val dir = getCacheDir(path)
        return flattenDir(dir)
    }

    private fun isInternalFile(file: File): Boolean {
        val name = file.name
        return name == ITEM_FILENAME
            || name == AUDIO_FILENAME
            || name == ARTWORK_SMALL_FILENAME
            || name == ARTWORK_LARGE_FILENAME
            || name == ARTWORK_NATIVE_FILENAME
            || name == META_FILENAME
    }

    private fun containsAudio(file: File): Boolean {
        if (!file.isDirectory) {
            return file.name == AUDIO_FILENAME
        }
        val files = file.listFiles() ?: return false

        for (child in files) {
            if (containsAudio(child)) {
                return true
            }
        }
        return false
    }

    private fun flattenDir(source: File): List<CollectionItem>? {
        val out = mutableListOf<CollectionItem>()

        val files = source.listFiles() ?: return out
        for (file in files) {
            try {
                if (isInternalFile(file)) {
                    continue
                }
                val item = readItem(file) ?: continue
                if (item.isDirectory) {
                    val content = flattenDir(file)
                    content?.run(out::addAll)
                } else if (hasAudio(item.path)) {
                    out.add(item)
                }
            } catch (e: IOException) {
                println("Error while reading offline cache: $e")
                return null
            } catch (e: ClassNotFoundException) {
                println("Error while reading offline cache: $e")
                return null
            }
        }
        return out
    }

    @Throws(IOException::class, SerializationException::class)
    private fun readItem(dir: File): CollectionItem? {
        val itemFile = File(dir, ITEM_FILENAME)
        if (!itemFile.exists()) {
            return if (dir.isDirectory) {
                val path = root.toURI().relativize(dir.toURI()).path
                CollectionItem.Directory(path = path)
            } else {
                null
            }
        }
        FileInputStream(itemFile).use { fis ->
            @OptIn(ExperimentalSerializationApi::class)
            return IO.cbor.decodeFromByteArray(CollectionItem.Serializer, fis.readBytes())
        }
    }

    private fun broadcast(event: String) {
        val application = PolarisApp.instance
        val intent = Intent()
        intent.action = event
        application.sendBroadcast(intent)
    }

    private enum class CacheDataType {
        ITEM,
        AUDIO,
        ARTWORK_SMALL,
        ARTWORK_LARGE,
        ARTWORK_NATIVE,
        META,
    }

    private class DeletionCandidate(
        val cachePath: File,
        val metadata: ItemCacheMetadata,
        val item: CollectionItem?
    )

    @Serializable
    private class ItemCacheMetadata(
        var lastUse: Long = System.currentTimeMillis()
    ) {

        fun updateUse() {
            lastUse = System.currentTimeMillis()
        }
    }
}
