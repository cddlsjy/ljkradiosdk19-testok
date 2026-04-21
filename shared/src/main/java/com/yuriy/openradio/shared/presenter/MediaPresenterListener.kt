package com.yuriy.openradio.shared.presenter

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.yuriy.openradio.shared.media.PlaybackState

interface MediaPresenterListener {
    fun showProgressBar()
    fun handleMetadataChanged(metadata: MediaMetadata)
    fun handlePlaybackStateChanged(state: PlaybackState)
}
