package com.yuriy.openradio.shared.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

object MediaItemHelper {
    fun getBitrateField(mediaItem: MediaItem): Int {
        return 0
    }

    fun updateImage(context: Context, metadata: MediaMetadata, imageView: ImageView) {
    }

    fun updateBitrateView(bitrate: Int, bitrateView: TextView, isPlayable: Boolean) {
    }
}
