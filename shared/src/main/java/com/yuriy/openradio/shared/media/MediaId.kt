package com.yuriy.openradio.shared.media

data class MediaId(
    val value: String,
    val category: String = ""
) {
    companion object {
        fun empty() = MediaId("")
    }
}
