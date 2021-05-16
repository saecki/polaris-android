package agersant.polaris.api.remote

import agersant.polaris.CollectionItem
import agersant.polaris.api.local.OfflineCache
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.TransferListener
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.RandomAccessFile

internal class PolarisExoPlayerDataSourceFactory constructor(
    private val offlineCache: OfflineCache,
    private val auth: Auth,
    private val scratchLocation: File,
    private val item: CollectionItem
) : DataSource.Factory {

    class PolarisHttpDataSource(
        private val offlineCache: OfflineCache,
        private val scratchLocation: File,
        private val item: CollectionItem,
        auth: Auth,
    ) : HttpDataSource {

        private inner class PolarisTransferListener : TransferListener {
            override fun onTransferInitializing(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) = Unit
            override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) = Unit
            override fun onBytesTransferred(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean, bytesTransferred: Int) = Unit

            override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
                val complete = dataSpec.position == dataSpec.length
                closeStreamFile(writeToOfflineCache = complete)
            }
        }

        private val inner: DefaultHttpDataSource
        private var streamFile: RandomAccessFile? = null

        init {
            val requestProperties = if (auth.cookieHeader != null) {
                mapOf("Cookie" to auth.cookieHeader)
            } else {
                mapOf("Authorization" to auth.authorizationHeader)
            }

            inner = DefaultHttpDataSource.Factory()
                .setTransferListener(PolarisTransferListener())
                .setDefaultRequestProperties(requestProperties)
                .createDataSource()

            try {
                if (scratchLocation.exists()) {
                    if (!scratchLocation.delete()) {
                        throw IOException("Could not clean the stream scratch location: $scratchLocation")
                    }
                }
                streamFile = RandomAccessFile(scratchLocation, "rw")
            } catch (e: Exception) {
                Log.e(PolarisHttpDataSource::class.qualifiedName, "Error while opening stream audio file: $e")
                throw e
            }
        }

        private fun closeStreamFile(writeToOfflineCache: Boolean) {
            try {
                streamFile?.close()
                streamFile = null
            } catch (e: Exception) {
                Log.e(PolarisHttpDataSource::class.qualifiedName, "Error while closing stream file: $e")
            }

            if (!writeToOfflineCache) return

            try {
                FileInputStream(scratchLocation).use {
                    offlineCache.putAudio(item, it)
                }
            } catch (e: Exception) {
                Log.e(PolarisHttpDataSource::class.qualifiedName, "Error while saving stream audio in offline cache: $e")
            }
        }

        private fun writeToStreamFile(buffer: ByteArray, offset: Int, length: Int) {
            if (length == 0) return
            if (length == C.RESULT_END_OF_INPUT) return

            try {
                streamFile?.write(buffer, offset, length)
            } catch (e: Exception) {
                Log.e(PolarisHttpDataSource::class.qualifiedName, "Error while writing audio to stream file: $e")
            }
        }

        override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
            val read = inner.read(buffer, offset, readLength)

            writeToStreamFile(buffer, offset, read)

            return read
        }

        override fun addTransferListener(transferListener: TransferListener) {
            inner.addTransferListener(transferListener)
        }

        override fun open(dataSpec: DataSpec): Long {
            return inner.open(dataSpec)
        }

        override fun getUri(): Uri? {
            return inner.uri
        }

        override fun getResponseHeaders(): MutableMap<String, MutableList<String>> {
            return inner.responseHeaders
        }

        override fun close() {
            inner.close()
        }

        override fun setRequestProperty(name: String, value: String) {
            inner.setRequestProperty(name, value)
        }

        override fun clearRequestProperty(name: String) {
            inner.clearRequestProperty(name)
        }

        override fun clearAllRequestProperties() {
            inner.clearAllRequestProperties()
        }

        override fun getResponseCode(): Int {
            return inner.responseCode
        }
    }

    override fun createDataSource(): HttpDataSource {
        return PolarisHttpDataSource(offlineCache, scratchLocation, item, auth)
    }
}
