package agersant.polaris.api.remote

import agersant.polaris.PolarisApp
import agersant.polaris.Song
import agersant.polaris.api.local.OfflineCache
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.BaseDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.util.*

class PolarisHttpDataSourceFactory internal constructor(
    serverAPI: ServerAPI,
    offlineCache: OfflineCache,
    scratchLocation: File,
    song: Song
) : DataSource.Factory {

    private val dataSource = PolarisHttpDataSource(serverAPI.authClient, offlineCache, scratchLocation, song)

    override fun createDataSource(): DataSource {
        return DefaultDataSource(PolarisApp.instance.applicationContext, dataSource)
    }

    private class PolarisHttpDataSource(
        private val currentClient: StateFlow<HttpClient?>,
        private val offlineCache: OfflineCache,
        private val scratchLocation: File,
        private val song: Song,
    ) : BaseDataSource(true) {

        private sealed class State {
            object Closed : State()
            class Opened(
                val dataSpec: DataSpec,
                val channel: ByteReadChannel,
                var bytesRemaining: Int,
                val contentLength: Int,
                val streamedBytes: BitSet,
                val streamFile: RandomAccessFile?,
            ) : State()
        }

        private var client: HttpClient? = null
        private var state: State = State.Closed

        init {
            PolarisApp.instance.scope.launch {
                currentClient.collect {
                    client = it
                    state = State.Closed
                }
            }
        }

        override fun open(dataSpec: DataSpec): Long {
            transferInitializing(dataSpec)

            val client = client ?: error("No authenticated client available")

            val response: HttpResponse = runBlocking { client.get(dataSpec.uri.toString()) }
            val contentLength = response.contentLength()?.toInt() ?: error("Unable to get content length")

            val channel = response.content
            val streamedBytes = BitSet(contentLength)

            val streamFile = try {
                RandomAccessFile(scratchLocation, "rw")
            } catch (e: Exception) {
                println("Error opening stream file: $scratchLocation")
                null
            }

            runBlocking { channel.discardExact(dataSpec.position) }

            val bytesRemaining = if (dataSpec.length == C.LENGTH_UNSET.toLong()) {
                contentLength - dataSpec.position.toInt()
            } else {
                dataSpec.length.toInt()
            }

            state = State.Opened(
                dataSpec,
                channel,
                bytesRemaining,
                contentLength,
                streamedBytes,
                streamFile,
            )

            transferStarted(dataSpec)

            return bytesRemaining.toLong()
        }

        override fun read(target: ByteArray, offset: Int, length: Int): Int = state.run {
            if (this !is State.Opened) error("Datasource is not opened")
            if (length == 0) return 0
            if (bytesRemaining == 0) return C.RESULT_END_OF_INPUT

            val bytesToRead = if (bytesRemaining == C.LENGTH_UNSET) length else minOf(bytesRemaining, length)
            val bytesRead = runBlocking { channel.readAvailable(target, offset, bytesToRead) }

            try {
                streamFile?.write(target, offset, bytesRead)
            } catch (e: Exception) {
                println("Error writing to stream file: $scratchLocation")
            }
            val currentPosition = contentLength - bytesRemaining
            streamedBytes.set(currentPosition, currentPosition + bytesRead)

            if (bytesRead == -1) {
                if (bytesRemaining != C.LENGTH_UNSET) {
                    throw EOFException()
                }
                return C.RESULT_END_OF_INPUT
            }

            if (bytesRemaining != C.LENGTH_UNSET) {
                bytesRemaining -= bytesRead
            }

            bytesTransferred(bytesRead)

            return bytesRead
        }

        override fun close() = state.run {
            if (this is State.Opened) {
                if (streamedBytes.nextClearBit(0) >= contentLength) {
                    println("Streaming complete, saving file for local use: " + song.path)
                    try {
                        streamFile?.close()
                    } catch (e: Exception) {
                        println("Error while closing stream audio file: $e")
                    }
                    try {
                        FileInputStream(scratchLocation).use { scratchFile ->
                            offlineCache.putAudio(song, scratchFile)
                        }
                    } catch (e: Exception) {
                        println("Error while saving stream audio in offline cache: $e")
                    }
                }

                channel.cancel()
                streamFile?.close()
                state = State.Closed
                transferEnded()
            }
        }

        override fun getUri(): Uri? = (state as? State.Opened)?.dataSpec?.uri
    }
}
