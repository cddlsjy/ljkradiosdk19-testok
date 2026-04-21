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

package com.yuriy.openradio.shared.model.media

/**
 * Media stream data class containing bitrate and stream URL information.
 */
class MediaStream(
    var bitRate: Int = BIT_RATE_DEFAULT,
    var streamUrl: String = ""
) {

    val isEmpty: Boolean
        get() = streamUrl.isEmpty()

    companion object {
        const val BIT_RATE_DEFAULT = 0
        const val BIT_RATE_MIN = 32
        const val BIT_RATE_MAX = 320
    }
}

/**
 * Extension function to set variant (bitrate and stream URL) for a radio station.
 */
fun setVariant(radioStation: RadioStation, bitRate: Int, streamUrl: String) {
    radioStation.mediaStream.bitRate = bitRate
    radioStation.mediaStream.streamUrl = streamUrl
}