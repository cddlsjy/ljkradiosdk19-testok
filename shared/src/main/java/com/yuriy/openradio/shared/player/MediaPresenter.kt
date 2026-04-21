package com.yuriy.openradio.shared.player

import com.yuriy.openradio.shared.media.MediaItem
import com.yuriy.openradio.shared.media.MediaMetadata
import com.yuriy.openradio.shared.media.PlaybackState

interface MediaPresenter {
    interface MediaPresenterListener {
        fun showProgressBar()
        fun hideProgressBar()
        fun handlePlaybackStateChanged(state: PlaybackState)
        fun handleMetadataChanged(metadata: MediaMetadata)
        fun onError(message: String)
    }

    fun play(mediaItem: MediaItem)
    fun pause()
    fun stop()
    fun seekTo(position: Long)
    fun getCurrentPosition(): Long
    fun getPlaybackState(): PlaybackState
    fun setListener(listener: MediaPresenterListener?)
}
