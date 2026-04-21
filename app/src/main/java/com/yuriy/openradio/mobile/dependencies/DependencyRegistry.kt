package com.yuriy.openradio.mobile.dependencies

import com.yuriy.openradio.mobile.view.activity.MainActivity
import com.yuriy.openradio.shared.dependencies.FavoritesStorageDependency
import com.yuriy.openradio.shared.dependencies.LatestRadioStationStorageDependency
import com.yuriy.openradio.shared.storage.FavoritesStorage
import com.yuriy.openradio.shared.storage.LatestRadioStationStorage

object DependencyRegistry : FavoritesStorageDependency, LatestRadioStationStorageDependency {
    fun inject(activity: MainActivity) {
        injectFavoritesStorage()
        injectLatestRadioStationStorage()
    }
    
    override fun injectFavoritesStorage() {
    }
    
    override fun injectLatestRadioStationStorage() {
    }
    
    fun configureWith(favoritesStorage: FavoritesStorage) {
    }
    
    fun configureWith(latestRadioStationStorage: LatestRadioStationStorage) {
    }
}
