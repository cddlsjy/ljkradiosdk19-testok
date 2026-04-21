package com.yuriy.openradio.shared.dependencies

import android.content.Context
import com.yuriy.openradio.shared.model.storage.FavoritesStorage
import com.yuriy.openradio.shared.model.storage.RadioStationsStorage
import com.yuriy.openradio.shared.presenter.MediaPresenter
import com.yuriy.openradio.shared.service.location.Country
import com.yuriy.openradio.shared.storage.LatestRadioStationStorage
import com.yuriy.openradio.shared.storage.LatestRadioStationStorageImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Central dependency registry for the application.
 * Manages shared dependencies and provides access to storage and player components.
 */
object DependencyRegistryCommon {

    private var mContext: Context? = null
    private val mDependencyScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Storage instances
    private var mFavoritesStorage: FavoritesStorage? = null
    private var mRadioStationsStorage: RadioStationsStorage? = null

    // Player instances
    private var mMediaPresenter: MediaPresenter? = null

    // State management
    private val _currentStationId = MutableStateFlow<String?>(null)
    val currentStationId: StateFlow<String?> = _currentStationId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    val isGoogleApiAvailable: Boolean
        get() = true

    /**
     * Initialize the dependency registry with application context.
     */
    fun init(context: Context) {
        mContext = context.applicationContext
    }

    /**
     * Get the application context.
     */
    fun getContext(): Context? = mContext

    /**
     * Get the dependency scope for coroutines.
     */
    fun getDependencyScope(): CoroutineScope = mDependencyScope

    // Favorites Storage
    fun getFavoritesStorage(): FavoritesStorage {
        if (mFavoritesStorage == null) {
            mContext?.let { context ->
                mFavoritesStorage = FavoritesStorage(WeakReference(context))
            }
        }
        return mFavoritesStorage ?: throw IllegalStateException("Context not initialized")
    }

    fun injectFavoritesStorage() {
        // Injection point for favorites storage
        getFavoritesStorage()
    }

    // Radio Stations Storage
    fun getRadioStationsStorage(countryCode: String = Country.getCountryCode()): RadioStationsStorage {
        mContext?.let { context ->
            return RadioStationsStorage(WeakReference(context), countryCode)
        }
        throw IllegalStateException("Context not initialized")
    }

    // Latest Radio Station Storage
    fun getLatestRadioStationStorage(): LatestRadioStationStorage {
        mContext?.let { context ->
            return LatestRadioStationStorageImpl(context)
        }
        throw IllegalStateException("Context not initialized")
    }

    // Media Presenter
    fun getMediaPresenter(): MediaPresenter {
        if (mMediaPresenter == null) {
            mMediaPresenter = MediaPresenter()
        }
        return mMediaPresenter ?: throw IllegalStateException("Presenter not initialized")
    }

    fun configureWith(mediaPresenter: MediaPresenter) {
        // Configure presenter with dependencies
        mMediaPresenter = mediaPresenter
    }

    // State management helpers
    fun setCurrentStationId(stationId: String?) {
        _currentStationId.value = stationId
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    /**
     * Clean up resources when no longer needed.
     */
    fun destroy() {
        mMediaPresenter?.destroy()
        mMediaPresenter = null
        mFavoritesStorage = null
        mContext = null
    }
}
