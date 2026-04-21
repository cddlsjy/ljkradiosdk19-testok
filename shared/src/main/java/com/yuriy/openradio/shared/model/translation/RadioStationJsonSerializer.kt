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

package com.yuriy.openradio.shared.model.translation

import com.yuriy.openradio.shared.model.media.MediaStream
import com.yuriy.openradio.shared.model.media.RadioStation

/**
 * JSON serializer for RadioStation objects.
 */
class RadioStationJsonSerializer {

    companion object {
        private const val JSON_ID = "Id"
        private const val JSON_NAME = "Name"
        private const val JSON_BITRATE = "Bitrate"
        private const val JSON_COUNTRY = "Country"
        private const val JSON_GENRE = "Genre"
        private const val JSON_IMAGE_URL = "ImgUrl"
        private const val JSON_STREAM_URL = "StreamUrl"
        private const val JSON_STATUS = "Status"
        private const val JSON_THUMB_URL = "ThumbUrl"
        private const val JSON_WEBSITE = "Website"
        private const val JSON_IS_LOCAL = "IsLocal"
        private const val JSON_SORT_ID = "SortId"
    }

    /**
     * Converts a RadioStation to JSON string.
     */
    fun toJson(station: RadioStation): String {
        val sb = StringBuilder()
        sb.append("{")
        sb.append("\"$JSON_ID\":${station.id},")
        sb.append("\"$JSON_NAME\":\"${escapeJson(station.name)}\",")
        sb.append("\"$JSON_BITRATE\":\"${station.mediaStream.bitRate}\",")
        sb.append("\"$JSON_COUNTRY\":\"${station.countryCode}\",")
        sb.append("\"$JSON_GENRE\":\"${escapeJson(station.genre)}\",")
        sb.append("\"$JSON_IMAGE_URL\":\"${escapeJson(station.imageUrl)}\",")
        sb.append("\"$JSON_STREAM_URL\":\"${escapeJson(station.mediaStream.streamUrl)}\",")
        sb.append("\"$JSON_STATUS\":${station.status},")
        sb.append("\"$JSON_WEBSITE\":\"${escapeJson(station.homePage)}\",")
        sb.append("\"$JSON_IS_LOCAL\":${station.isLocal},")
        sb.append("\"$JSON_SORT_ID\":${station.sortId}")
        sb.append("}")
        return sb.toString()
    }

    /**
     * Parses a JSON string to RadioStation.
     */
    fun fromJson(json: String): RadioStation? {
        return try {
            val station = RadioStation()
            station.id = extractStringValue(json, JSON_ID) ?: ""
            station.name = extractStringValue(json, JSON_NAME) ?: ""
            station.countryCode = extractStringValue(json, JSON_COUNTRY) ?: ""
            station.genre = extractStringValue(json, JSON_GENRE) ?: ""
            station.imageUrl = extractStringValue(json, JSON_IMAGE_URL) ?: ""
            station.homePage = extractStringValue(json, JSON_WEBSITE) ?: ""
            station.sortId = extractIntValue(json, JSON_SORT_ID) ?: 0
            station.status = extractIntValue(json, JSON_STATUS) ?: 0
            station.isLocal = extractBooleanValue(json, JSON_IS_LOCAL) ?: false

            station.mediaStream = MediaStream()
            station.mediaStream.bitRate = extractStringValue(json, JSON_BITRATE)?.toIntOrNull() ?: MediaStream.BIT_RATE_DEFAULT
            station.mediaStream.streamUrl = extractStringValue(json, JSON_STREAM_URL) ?: ""

            station
        } catch (e: Exception) {
            null
        }
    }

    private fun escapeJson(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun extractStringValue(json: String, key: String): String? {
        val regex = """"$key"\s*:\s*"([^"]*)"""".toRegex()
        return regex.find(json)?.groupValues?.getOrNull(1)
    }

    private fun extractIntValue(json: String, key: String): Int? {
        val regex = """"$key"\s*:\s*(-?\d+)""".toRegex()
        return regex.find(json)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractBooleanValue(json: String, key: String): Boolean? {
        val regex = """"$key"\s*:\s*(true|false)""".toRegex()
        return regex.find(json)?.groupValues?.getOrNull(1)?.toBoolean()
    }
}