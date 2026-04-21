/*
 * Copyright 2017-2021 The "Open Radio" Project. Author: Chernyshov Yuriy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yuriy.openradio.shared.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.yuriy.openradio.mobile.view.activity.MainActivity
import com.yuriy.openradio.shared.R
import com.yuriy.openradio.shared.media.PlaybackState
import com.yuriy.openradio.shared.utils.AppLogger

/**
 * Media playback service using Media3.
 * Handles audio playback with notification controls and media session integration.
 */
class RadioPlayerService : MediaSessionService() {

    private var mMediaSession: MediaSession? = null
    private var mPlayer: ExoPlayer? = null
    private lateinit var mNotificationManager: NotificationManager

    companion object {
        private const val TAG = "RadioPlayerService"
        private const val NOTIFICATION_CHANNEL_ID = "open_radio_playback"
        private const val NOTIFICATION_ID = 1

        const val ACTION_STOP = "com.yuriy.openradio.STOP"
    }

    override fun onCreate() {
        super.onCreate()
        AppLogger.d(TAG, "onCreate")

        initializePlayer()
        initializeMediaSession()
        createNotificationChannel()
    }

    private fun initializePlayer() {
        mPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true // Handle audio focus
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        mPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updateNotification()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification()
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                updateNotification()
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                AppLogger.e(TAG, "Player error: ${error.message}")
            }
        })
    }

    private fun initializeMediaSession() {
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mMediaSession = MediaSession.Builder(this, mPlayer!!)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
            }
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        // Media3 handles notification automatically with MediaSession
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot("root", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ): ListenableFuture<MutableList<MediaBrowserCompat.MediaItem>> {
        return Futures.immediateFuture(mutableListOf())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mMediaSession?.player
        if (player != null) {
            if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        AppLogger.d(TAG, "onDestroy")
        mMediaSession?.run {
            player.release()
            release()
            mMediaSession = null
        }
        mPlayer = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            mPlayer?.stop()
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, RadioPlayerService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playbackState = mPlayer?.let {
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_STOP or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
                .setState(
                    if (it.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                    0,
                    1f
                )
                .build()
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_playing))
            .setSmallIcon(R.drawable.ic_radio)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_pause, getString(R.string.pause), null)
            .addAction(R.drawable.ic_stop, getString(R.string.stop), stopIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1)
            )
            .setOngoing(mPlayer?.isPlaying == true)
            .build()
    }
}
