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

package com.yuriy.openradio.shared.model.storage.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference

/**
 * Provider class for managing radio station images with caching.
 *
 * @param contextRef Weak reference to the Android context
 */
class ImagesProvider(
    private val contextRef: WeakReference<android.content.Context>
) {

    companion object {
        private const val CACHE_DIR = "radio_images"
        private const val MAX_CACHE_SIZE = 50 * 1024 * 1024 // 50 MB
    }

    /**
     * Gets the image cache directory.
     */
    private fun getCacheDir(): File? {
        val context = contextRef.get() ?: return null
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }

    /**
     * Gets the file name for a given image URL.
     */
    private fun getFileName(imageUrl: String): String {
        return imageUrl.hashCode().toString() + ".png"
    }

    /**
     * Loads an image from the cache or from URL.
     */
    fun getImage(imageUrl: String): Bitmap? {
        val cacheDir = getCacheDir() ?: return null
        val file = File(cacheDir, getFileName(imageUrl))

        if (file.exists()) {
            return BitmapFactory.decodeFile(file.absolutePath)
        }

        return null
    }

    /**
     * Saves an image to the cache.
     */
    fun saveImage(imageUrl: String, bitmap: Bitmap): Boolean {
        val cacheDir = getCacheDir() ?: return false
        val file = File(cacheDir, getFileName(imageUrl))

        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Clears the image cache.
     */
    fun clearCache() {
        val cacheDir = getCacheDir() ?: return
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    /**
     * Gets the total size of the cache.
     */
    fun getCacheSize(): Long {
        val cacheDir = getCacheDir() ?: return 0
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0
    }

    /**
     * Trims the cache to stay within the maximum size.
     */
    fun trimCache() {
        val cacheDir = getCacheDir() ?: return
        val files = cacheDir.listFiles() ?: return

        var totalSize = files.sumOf { it.length() }
        if (totalSize <= MAX_CACHE_SIZE) return

        // Sort by last modified time, oldest first
        val sortedFiles = files.sortedBy { it.lastModified() }

        for (file in sortedFiles) {
            if (totalSize <= MAX_CACHE_SIZE) break
            totalSize -= file.length()
            file.delete()
        }
    }
}