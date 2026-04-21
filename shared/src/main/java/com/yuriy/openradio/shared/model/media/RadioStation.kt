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

import com.yuriy.openradio.shared.service.location.Country

/**
 * Radio station data class representing a radio station with all its properties.
 */
class RadioStation {

    var id: String = ""
    var name: String = ""
    var countryCode: String = Country.COUNTRY_CODE_DEFAULT
    var genre: String = ""
    var imageUrl: String = ""
    var homePage: String = ""
    var sortId: Int = 0
    var status: Int = STATUS_DEFAULT
    var isLocal: Boolean = false

    var mediaStream = MediaStream()

    val isEmpty: Boolean
        get() = id.isEmpty() || mediaStream.isEmpty

    companion object {
        const val STATUS_DEFAULT = 0
        const val STATUS_FAVORITE = 1

        /**
         * Creates a default instance of RadioStation with a given ID.
         */
        fun makeDefaultInstance(id: String): RadioStation {
            val station = RadioStation()
            station.id = id
            return station
        }
    }
}