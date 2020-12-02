package agersant.polaris

import agersant.polaris.api.API
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
import java.io.*
import java.lang.ref.WeakReference
import java.util.*

class PolarisPlaybackService : Service() {
    private val binder: IBinder = PolarisBinder()
    private lateinit var receiver: BroadcastReceiver
    private lateinit var audioManager: AudioManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var autoSaveHandler: Handler
    private lateinit var autoSaveRunnable: Runnable
    private lateinit var mediaSessionUpdateRunnable: Runnable
    private lateinit var mediaSessionUpdateHandler: Handler
    private lateinit var api: API
    private lateinit var player: PolarisPlayer
    private lateinit var playbackQueue: PlaybackQueue
    private lateinit var mediaSession: MediaSessionCompat

    private var audioFocusRequest: AudioFocusRequest? = null
    private var notification: Notification? = null
    private var notificationItem: CollectionItem? = null

    private inner class MediaSessionCallback(private val player: PolarisPlayer) : MediaSessionCompat.Callback() {
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
        api = App.state.api
        player = App.state.player
        playbackQueue = App.state.playbackQueue
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
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Notifications for current song playing in Polaris."
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                notificationManager.createNotificationChannel(this)
                notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(this)
            }
        }

        val filter = IntentFilter().apply {
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(PolarisPlayer.PLAYING_TRACK)
            addAction(PolarisPlayer.PAUSED_TRACK)
            addAction(PolarisPlayer.RESUMED_TRACK)
            addAction(PolarisPlayer.PLAYBACK_ERROR)
            addAction(PolarisPlayer.COMPLETED_TRACK)
        }
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
        autoSaveHandler = Handler()
        autoSaveHandler.postDelayed(autoSaveRunnable, AUTO_SAVE_DELAY)

        pushSystemNotification()

        mediaSession = MediaSessionCompat(this, packageName)
        mediaSession.setCallback(MediaSessionCallback(player))
        mediaSession.isActive = true
        updateMediaSessionState(PlaybackStateCompat.STATE_NONE)
        mediaSessionUpdateRunnable = Runnable {
            updateMediaSessionState(if (player.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED)
            mediaSessionUpdateHandler.postDelayed(mediaSessionUpdateRunnable, MEDIA_SESSION_UPDATE_DELAY)
        }
        mediaSessionUpdateHandler = Handler()
        startMediaSessionUpdates()
    }

    private fun updateMediaSessionState(state: Int) {
        val builder = PlaybackStateCompat.Builder()
        builder.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        builder.setState(state, player.currentPosition.toLong(), 1f)
        mediaSession.setPlaybackState(builder.build())
        val metadataBuilder = MediaMetadataCompat.Builder()
        val currentItem = player.currentItem
        if (currentItem != null) {
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentItem.title)
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentItem.artist)
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentItem.album)
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, currentItem.albumArtist)
            metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, currentItem.trackNumber.toLong())
            metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, currentItem.discNumber.toLong())
        }
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player.duration.toLong())
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
        mediaSessionUpdateHandler.postDelayed(mediaSessionUpdateRunnable, MEDIA_SESSION_UPDATE_DELAY)
    }

    private fun stopMediaSessionUpdates() {
        mediaSessionUpdateHandler.removeCallbacksAndMessages(null)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private inner class PolarisBinder : Binder()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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
        if (intent == null || intent.action == null) {
            return
        }
        when (intent.action) {
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
            audioManager.requestAudioFocus(audioFocusRequest!!)
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest!!)
        }
    }

    private fun pushSystemNotification() {
        val isPlaying = player.isPlaying
        val item = player.currentItem ?: return

        // On tap action
        val stackBuilder = TaskStackBuilder.create(this)
            .addParentStack(MainActivity::class.java)
            .addNextIntent(Intent(this, MainActivity::class.java))
        val tapPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // On dismiss action
        val dismissIntent = Intent(this, PolarisPlaybackService::class.java)
        dismissIntent.action = MEDIA_INTENT_DISMISS
        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)

        // Create notification
        val notificationBuilder: Notification.Builder = if (Build.VERSION.SDK_INT > 25) {
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
        notificationBuilder.setShowWhen(false)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(item.title)
            .setContentText(item.artist)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContentIntent(tapPendingIntent)
            .setDeleteIntent(dismissPendingIntent).style = Notification.MediaStyle()
            .setShowActionsInCompactView()

        // Add album art
        if (item === notificationItem && notification != null && notification!!.getLargeIcon() != null) {
            notificationBuilder.setLargeIcon(notification!!.getLargeIcon())
        }
        if (item.artwork != null) {
            api.loadImage(item) { bitmap: Bitmap? ->
                if (item !== player.currentItem) {
                    return@loadImage
                }
                notificationBuilder.setLargeIcon(bitmap)
                emitNotification(notificationBuilder, item)
            }
        }

        // Add media control actions
        notificationBuilder.addAction(generateAction(R.drawable.baseline_skip_previous_24, R.string.player_next_track, MEDIA_INTENT_SKIP_PREVIOUS))
        if (isPlaying) {
            notificationBuilder.addAction(generateAction(R.drawable.baseline_pause_24, R.string.player_pause, MEDIA_INTENT_PAUSE))
        } else {
            notificationBuilder.addAction(generateAction(R.drawable.baseline_play_arrow_24, R.string.player_play, MEDIA_INTENT_PLAY))
        }
        notificationBuilder.addAction(generateAction(R.drawable.baseline_skip_next_24, R.string.player_previous_track, MEDIA_INTENT_SKIP_NEXT))

        // Emit notification
        emitNotification(notificationBuilder, item)
        if (isPlaying) {
            startForeground(MEDIA_NOTIFICATION, notification)
        } else {
            stopForeground(false)
        }
    }

    private fun emitNotification(notificationBuilder: Notification.Builder, item: CollectionItem) {
        notificationItem = item
        notification = notificationBuilder.build()
        notificationManager.notify(MEDIA_NOTIFICATION, notification)
    }

    private fun generateAction(icon: Int, text: Int, intentAction: String): Notification.Action {
        val intent = Intent(this, PolarisPlaybackService::class.java)
        intent.action = intentAction
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        return Notification.Action.Builder(Icon.createWithResource(this, icon), resources.getString(text), pendingIntent).build()
    }

    private class StateWriteTask(context: Context, private val state: PlaybackQueueState) : AsyncTask<Unit, Unit, Unit>() {

        private val contextWeakReference: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg objects: Unit) {
            val context = contextWeakReference.get() ?: return
            val storage = File(context.cacheDir, "playlist.v" + PlaybackQueueState.VERSION)
            try {
                FileOutputStream(storage).use { out ->
                    try {
                        ObjectOutputStream(out).use { objOut -> objOut.writeObject(state) }
                    } catch (e: IOException) {
                        println("Error while saving PlaybackQueueState object: $e")
                    }
                }
            } catch (e: IOException) {
                println("Error while writing PlaybackQueueState file: $e")
            }
        }

    }

    private fun saveStateToDisk() {
        // Gather state
        val state = PlaybackQueueState()
        state.queueContent = ArrayList()
        for (item in playbackQueue.content) {
            try {
                state.queueContent.add(item.clone())
            } catch (e: CloneNotSupportedException) {
                println("Error gathering PlaybackQueueState content: $e")
            }
        }
        state.queueOrdering = playbackQueue.ordering
        val currentItem = player.currentItem
        state.queueIndex = playbackQueue.content.indexOf(currentItem)
        state.trackProgress = player.positionRelative

        // Persist
        val writeState = StateWriteTask(this, state)
        writeState.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
    }

    private fun restoreStateFromDisk() {
        val storage = File(cacheDir, "playlist.v" + PlaybackQueueState.VERSION)
        try {
            FileInputStream(storage).use { `in` ->
                try {
                    ObjectInputStream(`in`).use { objIn ->
                        val state = objIn.readObject()
                        if (state is PlaybackQueueState) {
                            playbackQueue.content = state.queueContent
                            playbackQueue.ordering = state.queueOrdering
                            if (state.queueIndex >= 0) {
                                val currentItem = playbackQueue.getItem(state.queueIndex)
                                if (currentItem != null) {
                                    player.play(currentItem)
                                    player.pause()
                                    player.seekToRelative(state.trackProgress)
                                }
                            }
                        }
                    }
                } catch (e: ClassNotFoundException) {
                    println("Error while loading PlaybackQueueState object: $e")
                }
            }
        } catch (e: IOException) {
            println("Error while reading PlaybackQueueState file: $e")
        }
    }

    companion object {
        private const val MEDIA_NOTIFICATION = 100
        private const val MEDIA_INTENT_PAUSE = "MEDIA_INTENT_PAUSE"
        private const val MEDIA_INTENT_PLAY = "MEDIA_INTENT_PLAY"
        private const val MEDIA_INTENT_SKIP_NEXT = "MEDIA_INTENT_SKIP_NEXT"
        private const val MEDIA_INTENT_SKIP_PREVIOUS = "MEDIA_INTENT_SKIP_PREVIOUS"
        private const val MEDIA_INTENT_DISMISS = "MEDIA_INTENT_DISMISS"
        const val APP_INTENT_COLD_BOOT = "POLARIS_PLAYBACK_SERVICE_COLD_BOOT"
        private const val NOTIFICATION_CHANNEL_ID = "POLARIS_NOTIFICATION_CHANNEL_ID"
        private const val MEDIA_SESSION_UPDATE_DELAY: Long = 5000
        private const val AUTO_SAVE_DELAY: Long = 5000
    }
}