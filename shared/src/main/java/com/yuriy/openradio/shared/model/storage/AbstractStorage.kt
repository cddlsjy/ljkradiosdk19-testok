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

package com.yuriy.openradio.shared.model.storage

import com.yuriy.openradio.shared.model.media.RadioStation
import com.yuriy.openradio.shared.model.translation.RadioStationJsonSerializer
import java.lang.ref.WeakReference

/**
 * Abstract storage class for managing radio station data with JSON serialization.
 *
 * @param contextRef Weak reference to the Android context
 * @param prefKey SharedPreferences key for storing data
 */
abstract class AbstractStorage(
    protected val contextRef: WeakReference<android.content.Context>,
    protected val prefKey: String
) {

    private val serializer = RadioStationJsonSerializer()

    /**
     * Clears all stored data.
     */
    abstract fun clear()

    /**
     * Returns all stored radio stations.
     */
    abstract fun getAll(): List<RadioStation>

    /**
     * Adds all provided radio stations to storage.
     */
    abstract fun addAll(list: Collection<RadioStation>)

    /**
     * Parses a string value into a list of radio stations.
     * Format: "id<:>json<<::>>id<:>json..."
     */
    protected fun getAllFromString(value: String): List<RadioStation> {
        if (value.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<RadioStation>()
        val entries = value.split("<<::>>")

        for (entry in entries) {
            val parts = entry.split("<:>")
            if (parts.size >= 2) {
                val json = parts[1]
                val station = serializer.fromJson(json)
                if (station != null) {
                    result.add(station)
                }
            }
        }

        return result
    }

    /**
     * Converts a list of radio stations to a string format.
     * Format: "id<:>json<<::>>id<:>json..."
     */
    protected fun listToString(list: List<RadioStation>): String {
        if (list.isEmpty()) {
            return ""
        }

        return list.joinToString("<<::>>") { station ->
            "${station.id}<:>${serializer.toJson(station)}"
        }
    }

    /**
     * Gets the SharedPreferences instance.
     */
    protected fun getSharedPreferences(): android.content.SharedPreferences? {
        return contextRef.get()?.getSharedPreferences(prefKey, android.content.Context.MODE_PRIVATE)
    }
}