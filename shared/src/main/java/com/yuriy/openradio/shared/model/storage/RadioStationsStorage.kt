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
import java.lang.ref.WeakReference

/**
 * Storage class for managing radio stations data.
 *
 * @param contextRef Weak reference to the Android context
 * @param countryCode The country code for this storage
 */
class RadioStationsStorage(
    contextRef: WeakReference<android.content.Context>,
    private val countryCode: String
) : AbstractStorage(contextRef, PREF_KEY) {

    companion object {
        const val PREF_KEY = "radio_stations_storage"
        private const val KEY_STATIONS = "stations_"
    }

    override fun clear() {
        getSharedPreferences()?.edit()?.clear()?.apply()
    }

    override fun getAll(): List<RadioStation> {
        val value = getSharedPreferences()?.getString(KEY_STATIONS + countryCode, "") ?: ""
        return getAllFromString(value)
    }

    override fun addAll(list: Collection<RadioStation>) {
        val value = listToString(list.toList())
        getSharedPreferences()?.edit()?.putString(KEY_STATIONS + countryCode, value)?.apply()
    }
}
