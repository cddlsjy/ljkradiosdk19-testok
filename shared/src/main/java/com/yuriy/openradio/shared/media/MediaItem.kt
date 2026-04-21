package com.yuriy.openradio.shared.media

data class MediaItem(
    val mediaId: MediaId = MediaId.empty(),
    val title: String = "",
    val description: String = "",
    val uri: String = "",
    val thumbnailUri: String? = null,
    val bitrate: Int = 0,
    val isFavorite: Boolean = false
)
