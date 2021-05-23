package agersant.polaris

import agersant.polaris.api.API
import agersant.polaris.api.ThumbnailSize
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import java.io.*

class PolarisPlaybackService : LifecycleService() {

    companion object {
        const val APP_INTENT_COLD_BOOT = "POLARIS_PLAYBACK_SERVICE_COLD_BOOT"
        private const val MEDIA_NOTIFICATION = 100
        private const val MEDIA_INTENT_PAUSE = "MEDIA_INTENT_PAUSE"
        private const val MEDIA_INTENT_PLAY = "MEDIA_INTENT_PLAY"
        private const val MEDIA_INTENT_SKIP_NEXT = "MEDIA_INTENT_SKIP_NEXT"
        private const val MEDIA_INTENT_SKIP_PREVIOUS = "MEDIA_INTENT_SKIP_PREVIOUS"
        private const val MEDIA_INTENT_DISMISS = "MEDIA_INTENT_DISMISS"
        private const val NOTIFICATION_CHANNEL_ID = "POLARIS_NOTIFICATION_CHANNEL_ID"
        private const val MEDIA_SESSION_UPDATE_DELAY: Long = 5000
        private const val AUTO_SAVE_DELAY: Long = 5000
    }

    private lateinit var receiver: BroadcastReceiver
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var notification: Notification? = null
    private var notificationSong: Song? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var autoSaveHandler: Handler
    private lateinit var autoSaveRunnable: Runnable
    private lateinit var mediaSessionUpdateRunnable: Runnable
    private lateinit var mediaSessionUpdateHandler: Handler
    private lateinit var api: API
    private lateinit var player: PolarisPlayer
    private lateinit var playbackQueue: PlaybackQueue
    private lateinit var mediaSession: MediaSessionCompat

    private inner class MediaSessionCallback(
        private val player: PolarisPlayer,
    ) : MediaSessionCompat.Callback() {
        override fun onPause() {
            player.pause()
        }

        override fun onPlay() {
            player.resume()
        }

        override fun onSkipToNext() {
            player.skipNext()
        }

        override fun onSkipToPrevious() {
            player.skipPrevious()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val state = PolarisApp.state
        api = state.api
        player = state.player
        playbackQueue = state.playbackQueue
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= 26) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .build()
        }
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > 25) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                resources.getString(R.string.media_notifications_channel_description),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = "Notifications for current song playing in Polaris."
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val filter = IntentFilter()
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        filter.addAction(PolarisPlayer.PLAYING_TRACK)
        filter.addAction(PolarisPlayer.PAUSED_TRACK)
        filter.addAction(PolarisPlayer.RESUMED_TRACK)
        filter.addAction(PolarisPlayer.PLAYBACK_ERROR)
        filter.addAction(PolarisPlayer.COMPLETED_TRACK)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PolarisPlayer.PLAYBACK_ERROR -> {
                        stopMediaSessionUpdates()
                        updateMediaSessionState(PlaybackStateCompat.STATE_ERROR)
                        displayError()
                    }
                    PolarisPlayer.PLAYING_TRACK, PolarisPlayer.RESUMED_TRACK -> {
                        requestAudioFocus()
                        startMediaSessionUpdates()
                        updateMediaSessionState(PlaybackStateCompat.STATE_PLAYING)
                        pushSystemNotification()
                    }
                    PolarisPlayer.PAUSED_TRACK -> {
                        abandonAudioFocus()
                        stopMediaSessionUpdates()
                        updateMediaSessionState(PlaybackStateCompat.STATE_PAUSED)
                        pushSystemNotification()
                        saveStateToDisk()
                    }
                    PolarisPlayer.COMPLETED_TRACK -> {
                        abandonAudioFocus()
                        stopMediaSessionUpdates()
                        updateMediaSessionState(PlaybackStateCompat.STATE_STOPPED)
                    }
                    AudioManager.ACTION_AUDIO_BECOMING_NOISY -> player.pause()
                }
            }
        }
        registerReceiver(receiver, filter)
        autoSaveRunnable = Runnable {
            saveStateToDisk()
            autoSaveHandler.postDelayed(autoSaveRunnable, AUTO_SAVE_DELAY)
        }
        autoSaveHandler = Handler(Looper.getMainLooper())
        autoSaveHandler.postDelayed(autoSaveRunnable, AUTO_SAVE_DELAY)
        pushSystemNotification()
        mediaSession = MediaSessionCompat(this, packageName)
        mediaSession.setCallback(MediaSessionCallback(player))
        mediaSession.isActive = true
        updateMediaSessionState(PlaybackStateCompat.STATE_NONE)
        mediaSessionUpdateRunnable = Runnable {
            updateMediaSessionState(if (player.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED)
            mediaSessionUpdateHandler.postDelayed(
                mediaSessionUpdateRunnable,
                MEDIA_SESSION_UPDATE_DELAY
            )
        }
        mediaSessionUpdateHandler = Handler(Looper.getMainLooper())
        startMediaSessionUpdates()
    }

    private fun updateMediaSessionState(state: Int) {
        val builder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
            .setState(state, player.currentPosition.toLong(), 1f)

        mediaSession.setPlaybackState(builder.build())
        val metadataBuilder = MediaMetadataCompat.Builder()
        val currentSong = player.currentSong
        if (currentSong != null) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.title)
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.artist)
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.album)
            metadataBuilder.putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                currentSong.albumArtist
            )
            metadataBuilder.putLong(
                MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                currentSong.trackNumber.toLong()
            )
            metadataBuilder.putLong(
                MediaMetadataCompat.METADATA_KEY_DISC_NUMBER,
                currentSong.discNumber.toLong()
            )
            metadataBuilder.putLong(
                MediaMetadataCompat.METADATA_KEY_YEAR,
                currentSong.year.toLong()
            )
        }
        metadataBuilder.putLong(
            MediaMetadataCompat.METADATA_KEY_DURATION, player.duration
            .toLong()
        )
        mediaSession.setMetadata(metadataBuilder.build())
    }

    override fun onDestroy() {
        mediaSession.release()
        unregisterReceiver(receiver)
        autoSaveHandler.removeCallbacksAndMessages(null)
        stopMediaSessionUpdates()
        super.onDestroy()
    }

    private fun startMediaSessionUpdates() {
        stopMediaSessionUpdates()
        mediaSessionUpdateHandler.postDelayed(
            mediaSessionUpdateRunnable,
            MEDIA_SESSION_UPDATE_DELAY
        )
    }

    private fun stopMediaSessionUpdates() {
        mediaSessionUpdateHandler.removeCallbacksAndMessages(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    // Internals
    private fun displayError() {
        val toast = Toast.makeText(this, R.string.playback_error, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            APP_INTENT_COLD_BOOT -> restoreStateFromDisk()
            MEDIA_INTENT_PAUSE -> player.pause()
            MEDIA_INTENT_PLAY -> player.resume()
            MEDIA_INTENT_SKIP_NEXT -> player.skipNext()
            MEDIA_INTENT_SKIP_PREVIOUS -> player.skipPrevious()
            MEDIA_INTENT_DISMISS -> stopSelf()
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            audioFocusRequest?.let(audioManager::requestAudioFocus)
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            audioFocusRequest?.let(audioManager::abandonAudioFocusRequest)
        }
    }

    private fun pushSystemNotification() {
        val isPlaying = player.isPlaying
        val song = player.currentSong ?: return

        // On tap action
        val tapPendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.app)
            .setDestination(R.id.nav_now_playing)
            .createPendingIntent()

        // On dismiss action
        val dismissIntent = Intent(this, PolarisPlaybackService::class.java)
        dismissIntent.action = MEDIA_INTENT_DISMISS
        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)

        // Create notification
        val notificationBuilder = if (Build.VERSION.SDK_INT > 25) {
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
        notificationBuilder.setShowWhen(false)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContentIntent(tapPendingIntent)
            .setDeleteIntent(dismissPendingIntent).style = Notification.MediaStyle()
            .setShowActionsInCompactView()

        // Add album art
        if (song === notificationSong && notification != null && notification?.getLargeIcon() != null) {
            notificationBuilder.setLargeIcon(notification?.getLargeIcon())
        }
        if (song.artwork != null) {
            api.loadThumbnail(song, ThumbnailSize.Small) { bitmap: Bitmap? ->
                if (song !== player.currentSong) {
                    return@loadThumbnail
                }
                notificationBuilder.setLargeIcon(bitmap)
                emitNotification(notificationBuilder, song)
            }
        }

        // Add media control actions
        notificationBuilder.addAction(
            generateAction(
                R.drawable.ic_skip_previous_24,
                R.string.player_next_track,
                MEDIA_INTENT_SKIP_PREVIOUS
            )
        )
        if (isPlaying) {
            notificationBuilder.addAction(
                generateAction(
                    R.drawable.ic_pause_24,
                    R.string.player_pause,
                    MEDIA_INTENT_PAUSE
                )
            )
        } else {
            notificationBuilder.addAction(
                generateAction(
                    R.drawable.ic_play_arrow_24,
                    R.string.player_play,
                    MEDIA_INTENT_PLAY
                )
            )
        }
        notificationBuilder.addAction(
            generateAction(
                R.drawable.ic_skip_next_24,
                R.string.player_previous_track,
                MEDIA_INTENT_SKIP_NEXT
            )
        )

        // Emit notification
        emitNotification(notificationBuilder, song)
        if (isPlaying) {
            startForeground(MEDIA_NOTIFICATION, notification)
        } else {
            stopForeground(false)
        }
    }

    private fun emitNotification(notificationBuilder: Notification.Builder, song: Song) {
        notificationSong = song
        notification = notificationBuilder.build()
        notificationManager.notify(MEDIA_NOTIFICATION, notification)
    }

    private fun generateAction(icon: Int, text: Int, intentAction: String): Notification.Action {
        val intent = Intent(this, PolarisPlaybackService::class.java)
        intent.action = intentAction
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        return Notification.Action.Builder(
            Icon.createWithResource(this, icon),
            resources.getString(text),
            pendingIntent
        ).build()
    }

    private fun saveStateToDisk() {
        val state = PlaybackQueue.State(
            queueOrdering = playbackQueue.ordering,
            queueContent = playbackQueue.content,
            queueIndex = playbackQueue.content.indexOf(player.currentSong),
            trackProgress = player.positionRelative,
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val storage = File(cacheDir, "playlist.v${PlaybackQueue.State.VERSION}")
            try {
                FileOutputStream(storage).use { fos ->
                    @OptIn(ExperimentalSerializationApi::class)
                    val bytes = Serializers.cbor.encodeToByteArray(PlaybackQueue.State.serializer(), state)
                    fos.write(bytes)
                }
            } catch (e: IOException) {
                println("Error while writing PlaybackQueueState file: $e")
            }
        }
    }

    private fun restoreStateFromDisk() {
        val storage = File(cacheDir, "playlist.v" + PlaybackQueue.State.VERSION)
        try {
            FileInputStream(storage).use { fis ->
                try {
                    @OptIn(ExperimentalSerializationApi::class)
                    val state = Serializers.cbor.decodeFromByteArray(PlaybackQueue.State.serializer(), fis.readBytes())

                    state.run {
                        playbackQueue.ordering = queueOrdering
                        playbackQueue.content = queueContent
                        if (queueIndex >= 0) {
                            val currentSong = playbackQueue.getItem(queueIndex)
                            if (currentSong != null) {
                                player.play(currentSong)
                                player.pause()
                                player.seekToRelative(trackProgress)
                            }
                        }
                    }
                } catch (e: SerializationException) {
                    println("Error while loading PlaybackQueueState object: $e")
                }
            }
        } catch (e: IOException) {
            println("Error while reading PlaybackQueueState file: $e")
        }
    }
}
