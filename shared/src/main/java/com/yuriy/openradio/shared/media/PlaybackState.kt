package com.yuriy.openradio.shared.media

data class PlaybackState(
    val state: Int = STATE_IDLE,
    val position: Long = 0L,
    val duration: Long = 0L
) {
    companion object {
        const val STATE_IDLE = 0
        const val STATE_BUFFERING = 1
        const val STATE_READY = 2
        const val STATE_ENDED = 3
    }
}
