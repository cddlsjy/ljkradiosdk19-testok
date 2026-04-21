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

/**
 * Data class representing equalizer settings.
 */
class Equalizer {
    var presetId: Int = 0
    var customBands: IntArray = IntArray(5) { 0 }
        get() = field.copyOf()
        set(value) {
            field = value.copyOf()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Equalizer
        if (presetId != other.presetId) return false
        if (!customBands.contentEquals(other.customBands)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = presetId
        result = 31 * result + customBands.contentHashCode()
        return result
    }

    companion object {
        const val PRESET_NORMAL = 0
        const val PRESET_POP = 1
        const val PRESET_ROCK = 2
        const val PRESET_JAZZ = 3
        const val PRESET_CLASSICAL = 4
        const val PRESET_CUSTOM = 5
    }
}

/**
 * JSON serializer for Equalizer objects.
 */
class EqualizerSerialization {

    companion object {
        private const val JSON_PRESET_ID = "presetId"
        private const val JSON_CUSTOM_BANDS = "customBands"
    }

    /**
     * Converts an Equalizer to JSON string.
     */
    fun toJson(equalizer: Equalizer): String {
        val sb = StringBuilder()
        sb.append("{")
        sb.append("\"$JSON_PRESET_ID\":${equalizer.presetId},")
        sb.append("\"$JSON_CUSTOM_BANDS\":[")
        sb.append(equalizer.customBands.joinToString(","))
        sb.append("]")
        sb.append("}")
        return sb.toString()
    }

    /**
     * Parses a JSON string to Equalizer.
     */
    fun fromJson(json: String): Equalizer? {
        return try {
            val equalizer = Equalizer()

            val presetRegex = """"$JSON_PRESET_ID"\s*:\s*(-?\d+)""".toRegex()
            presetRegex.find(json)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let {
                equalizer.presetId = it
            }

            val bandsRegex = """"$JSON_CUSTOM_BANDS"\s*:\s*\[([^\]]+)]""".toRegex()
            bandsRegex.find(json)?.groupValues?.getOrNull(1)?.let { bandsStr ->
                val bands = bandsStr.split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .take(5)
                    .toIntArray()
                equalizer.customBands = bands
            }

            equalizer
        } catch (e: Exception) {
            null
        }
    }
}