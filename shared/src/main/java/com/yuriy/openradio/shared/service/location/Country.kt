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

package com.yuriy.openradio.shared.service.location

import android.content.Context
import java.util.Locale

/**
 * Country data class representing a country with code and name.
 */
class Country(
    val code: String,
    val name: String
) {
    companion object {
        const val COUNTRY_CODE_DEFAULT = "US"

        /**
         * Gets the country code from the device's locale.
         */
        fun getCountryCode(): String {
            return Locale.getDefault().country
        }

        /**
         * Gets a Country object from a country code.
         */
        fun fromCode(code: String): Country {
            val locale = Locale("", code)
            return Country(code, locale.displayCountry)
        }
    }
}