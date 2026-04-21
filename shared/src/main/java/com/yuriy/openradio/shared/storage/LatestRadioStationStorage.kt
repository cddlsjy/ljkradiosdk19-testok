package com.yuriy.openradio.shared.storage

import android.content.Context

interface LatestRadioStationStorage {
    fun setLastStationId(mediaId: String)
    fun getLastStationId(): String?
}

class LatestRadioStationStorageImpl(context: Context) : LatestRadioStationStorage {
    private val prefs = context.getSharedPreferences("latest_station", Context.MODE_PRIVATE)

    override fun setLastStationId(mediaId: String) {
        prefs.edit().putString("latest", mediaId).apply()
    }

    override fun getLastStationId(): String? {
        return prefs.getString("latest", null)
    }
}
