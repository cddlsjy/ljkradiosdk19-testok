package com.yuriy.openradio.shared.storage

import android.content.Context

interface FavoritesStorage {
    fun addFavorite(mediaId: String)
    fun removeFavorite(mediaId: String)
    fun isFavorite(mediaId: String): Boolean
    fun getAllFavorites(): List<String>
}

class FavoritesStorageImpl(context: Context) : FavoritesStorage {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    override fun addFavorite(mediaId: String) {
        prefs.edit().putBoolean(mediaId, true).apply()
    }

    override fun removeFavorite(mediaId: String) {
        prefs.edit().remove(mediaId).apply()
    }

    override fun isFavorite(mediaId: String): Boolean {
        return prefs.getBoolean(mediaId, false)
    }

    override fun getAllFavorites(): List<String> {
        return prefs.all.keys.toList()
    }
}
