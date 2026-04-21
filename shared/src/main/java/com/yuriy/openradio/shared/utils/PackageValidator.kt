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

package com.yuriy.openradio.shared.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * Utility class for package validation and checking.
 */
object PackageValidator {

    /**
     * Checks if a package is installed on the device.
     *
     * @param context The Android context
     * @param packageName The package name to check
     * @return True if the package is installed, false otherwise
     */
    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Checks if a package is installed and enabled.
     *
     * @param context The Android context
     * @param packageName The package name to check
     * @return True if the package is installed and enabled, false otherwise
     */
    fun isPackageEnabled(context: Context, packageName: String): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            context.packageManager.getApplicationEnabledSetting(packageName) !=
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Gets the version code of an installed package.
     *
     * @param context The Android context
     * @param packageName The package name
     * @return The version code, or -1 if not found
     */
    fun getPackageVersionCode(context: Context, packageName: String): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }
    }

    /**
     * Gets the version name of an installed package.
     *
     * @param context The Android context
     * @param packageName The package name
     * @return The version name, or null if not found
     */
    fun getPackageVersionName(context: Context, packageName: String): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    /**
     * Common package names for Google Play services and Cast framework.
     */
    object Packages {
        const val GOOGLE_PLAY_SERVICES = "com.google.android.gms"
        const val GOOGLE_PLAY_SERVICES_CAST = "com.google.android.gms.cast.framework"
        const val CHROMECAST_RECEIVER = "com.google.android.apps.cast.tv.framework"
    }
}