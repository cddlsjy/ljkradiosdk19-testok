/*
 * Copyright 2017-2020 The "Open Radio" Project. Author: Chernyshov Yuriy
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

package com.yuriy.openradio.shared.utils

import com.yuriy.openradio.shared.service.location.Country

/**
 * Helper class for parsing and manipulating media IDs.
 * Media IDs follow format: PREFIX + countryCode (e.g., "countries_list_US")
 */
object MediaIDHelper {

    /**
     * Extracts the country code from a media ID.
     *
     * @param mediaId The media ID to parse
     * @param defaultCode The default country code to return if not found
     * @return The country code or the default value
     */
    fun getCountryCode(mediaId: String?, defaultCode: String): String? {
        if (mediaId == null) {
            return null
        }

        val codes = listOf(
            "US", "CA", "GB", "AU", "DE", "FR", "JP", "KR", "CN", "IN",
            "BR", "MX", "ES", "IT", "NL", "SE", "NO", "DK", "FI", "PL",
            "RU", "UA", "TR", "IL", "AE", "SA", "EG", "ZA", "NG", "KE",
            "TH", "VN", "ID", "MY", "SG", "PH", "NZ", "AR", "CL", "CO"
        )

        for (code in codes) {
            if (mediaId.endsWith(code) && mediaId.length > code.length) {
                val prefix = mediaId.substring(0, mediaId.length - code.length)
                if (prefix.contains("countries") || prefix.contains("list")) {
                    return code
                }
            }
        }

        return if (mediaId.contains("countries")) defaultCode else null
    }

    /**
     * Extracts the base ID from a media ID (removes country code suffix).
     *
     * @param mediaId The media ID to parse
     * @param defaultId The default ID to return if not found
     * @return The base ID or the default value
     */
    fun getId(mediaId: String?, defaultId: String): String {
        if (mediaId == null) {
            return defaultId
        }

        val ids = listOf(
            MediaId.MEDIA_ID_CHILD_CATEGORIES,
            MediaId.MEDIA_ID_COUNTRIES_LIST,
            MediaId.MEDIA_ID_SEARCH_FROM_APP,
            MediaId.MEDIA_ID_FAVORITES,
            MediaId.MEDIA_ID_LATEST,
            MediaId.MEDIA_ID_CATEGORIES
        )

        for (id in ids) {
            if (mediaId.startsWith(id)) {
                return id
            }
        }

        return defaultId
    }

    /**
     * Builds a media ID with a country code suffix.
     *
     * @param baseId The base media ID
     * @param countryCode The country code to append
     * @return The complete media ID
     */
    fun buildMediaId(baseId: String, countryCode: String): String {
        return baseId + countryCode
    }

    /**
     * Checks if a media ID represents a root category.
     *
     * @param mediaId The media ID to check
     * @return True if it's a root category
     */
    fun isRootCategory(mediaId: String?): Boolean {
        return mediaId == MediaId.MEDIA_ID_CATEGORIES
    }

    /**
     * Checks if a media ID represents a child category.
     *
     * @param mediaId The media ID to check
     * @return True if it's a child category
     */
    fun isChildCategory(mediaId: String?): Boolean {
        return mediaId?.startsWith(MediaId.MEDIA_ID_CHILD_CATEGORIES) == true
    }

    /**
     * Checks if a media ID represents a countries list.
     *
     * @param mediaId The media ID to check
     * @return True if it's a countries list
     */
    fun isCountriesList(mediaId: String?): Boolean {
        return mediaId?.startsWith(MediaId.MEDIA_ID_COUNTRIES_LIST) == true
    }

    /**
     * Checks if a media ID represents search results.
     *
     * @param mediaId The media ID to check
     * @return True if it's search results
     */
    fun isSearchResults(mediaId: String?): Boolean {
        return mediaId?.startsWith(MediaId.MEDIA_ID_SEARCH_FROM_APP) == true
    }
}

/**
 * Object containing media ID constants.
 */
object MediaId {
    const val MEDIA_ID_CATEGORIES = "categories"
    const val MEDIA_ID_CHILD_CATEGORIES = "child_categories_"
    const val MEDIA_ID_COUNTRIES_LIST = "countries_list_"
    const val MEDIA_ID_SEARCH_FROM_APP = "search_from_app"
    const val MEDIA_ID_FAVORITES = "favorites"
    const val MEDIA_ID_LATEST = "latest"
}