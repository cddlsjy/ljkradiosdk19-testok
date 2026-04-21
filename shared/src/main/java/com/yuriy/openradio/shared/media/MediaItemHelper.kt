package com.yuriy.openradio.shared.media

object MediaItemHelper {
    fun getMediaId(item: MediaItem): String {
        return item.mediaId.value
    }

    fun getParentId(item: MediaItem): String {
        return item.mediaId.category
    }

    fun isValid(item: MediaItem): Boolean {
        return item.mediaId.value.isNotEmpty() && item.uri.isNotEmpty()
    }
}
