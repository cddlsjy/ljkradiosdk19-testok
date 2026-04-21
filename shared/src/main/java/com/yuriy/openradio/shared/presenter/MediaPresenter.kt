package com.yuriy.openradio.shared.presenter

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.android.gms.cast.framework.CastContext
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.yuriy.openradio.shared.broadcast.AppLocalReceiverCallback
import com.yuriy.openradio.shared.media.PlaybackState
import com.yuriy.openradio.shared.model.media.MediaItemsSubscription
import com.yuriy.openradio.shared.player.RadioPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Media presenter implementation that handles media playback using Media3.
 * Manages the MediaController, playback state, and UI updates.
 */
class MediaPresenter : Player.Listener {

    private var mContext: Context? = null
    private var mMainView: View? = null
    private var mListView: View? = null
    private var mCurrentRadioStationView: View? = null
    private var mAdapter: Any? = null
    private var mSubscriptionCb: MediaItemsSubscription? = null
    private var mListener: MediaPresenterListener? = null
    private var mBroadcastReceiverCb: AppLocalReceiverCallback? = null
    private var mSavedInstanceState: Bundle? = null

    private var mControllerFuture: ListenableFuture<MediaController>? = null
    private var mMediaController: MediaController? = null
    private var mCastContext: CastContext? = null

    private val mPresenterScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mPlaybackStateJob: Job? = null

    private var mOnSaveInstancePassed = false
    private var mCurrentMediaItem: MediaItem? = null

    // Flow-based state management
    private val _playbackState = MutableStateFlow(PlaybackState(PlaybackState.STATE_IDLE))
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentMetadata = MutableStateFlow<MediaMetadata?>(null)
    val currentMetadata: StateFlow<MediaMetadata?> = _currentMetadata.asStateFlow()

    fun init(
        activity: Context,
        mainView: View,
        savedInstanceState: Bundle?,
        listView: View,
        currentRadioStationView: View,
        adapter: Any,
        subscriptionCb: MediaItemsSubscription,
        listener: MediaPresenterListener,
        broadcastReceiverCb: AppLocalReceiverCallback
    ) {
        mContext = activity
        mMainView = mainView
        mListView = listView
        mCurrentRadioStationView = currentRadioStationView
        mAdapter = adapter
        mSubscriptionCb = subscriptionCb
        mListener = listener
        mBroadcastReceiverCb = broadcastReceiverCb
        mSavedInstanceState = savedInstanceState

        initializeMediaController()
        initializeCastContext()
        observePlaybackState()
    }

    private fun initializeMediaController() {
        val context = mContext ?: return
        val sessionToken = SessionToken(context, ComponentName(context, RadioPlayerService::class.java))
        mControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mControllerFuture?.addListener({
            try {
                mMediaController = mControllerFuture?.get()
                mMediaController?.addListener(this)
                restoreState()
            } catch (e: Exception) {
                // Handle initialization error
            }
        }, MoreExecutors.directExecutor())
    }

    private fun initializeCastContext() {
        try {
            mCastContext = CastContext.getSharedInstance(mContext!!)
        } catch (e: Exception) {
            // Cast not available
            mCastContext = null
        }
    }

    private fun observePlaybackState() {
        mPlaybackStateJob?.cancel()
        mPlaybackStateJob = mPresenterScope.launch {
            playbackState.collect { state ->
                mListener?.handlePlaybackStateChanged(state)
            }
        }
    }

    private fun restoreState() {
        val savedInstanceState = mSavedInstanceState ?: return
        // Restore last played station if available
        val lastStationId = savedInstanceState.getString(KEY_LAST_STATION_ID)
        if (lastStationId != null && mAdapter != null) {
            // Find and restore the station
            // This would typically be implemented with the adapter
        }
    }

    fun getCastContext(): CastContext? = mCastContext

    fun destroy() {
        mPresenterScope.cancel()
        mMediaController?.removeListener(this)
        mControllerFuture?.let { MediaController.releaseFuture(it) }
        mMediaController = null
        mContext = null
        mMainView = null
        mListView = null
        mCurrentRadioStationView = null
        mAdapter = null
        mSubscriptionCb = null
        mListener = null
        mBroadcastReceiverCb = null
    }

    fun handleResume() {
        // Reconnect to media service if needed
        initializeMediaController()
    }

    fun handleSaveInstanceState(outState: Bundle) {
        mCurrentMediaItem?.let {
            outState.putString(KEY_LAST_STATION_ID, it.mediaId)
        }
        outState.putBoolean(KEY_SAVE_INSTANCE_PASSED, true)
        mOnSaveInstancePassed = true
    }

    fun handleBackPressed(): Boolean {
        // Handle back press - pause playback
        pause()
        return true
    }

    fun getOnSaveInstancePassed(): Boolean = mOnSaveInstancePassed

    fun handleRemoveRadioStationMenu(view: View) {
        // Handle remove station menu action
    }

    fun handleEditRadioStationMenu(view: View) {
        // Handle edit station menu action
    }

    fun handleCurrentIndexOnQueueChanged(index: Int) {
        // Update current index in queue
    }

    fun handleChildrenLoaded(parentId: String, children: List<MediaItem>) {
        // Handle children loaded for browse navigation
    }

    fun handlePermissionsResult(permissions: Array<String>, grantResults: IntArray) {
        // Handle permission results
    }

    fun updateDescription(view: View, metadata: MediaMetadata) {
        _currentMetadata.value = metadata
        mListener?.handleMetadataChanged(metadata)
    }

    fun getCurrentMediaItem(): MediaItem? = mCurrentMediaItem

    fun getServiceCommander(): Any? = mMediaController

    fun isAdapterEmpty(): Boolean {
        return mAdapter == null
    }

    // Playback control methods
    fun play(mediaItem: MediaItem) {
        mCurrentMediaItem = mediaItem
        mMediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        updatePlaybackState(PlaybackState.STATE_BUFFERING)
    }

    fun pause() {
        mMediaController?.pause()
    }

    fun stop() {
        mMediaController?.stop()
        updatePlaybackState(PlaybackState.STATE_IDLE)
    }

    fun seekTo(position: Long) {
        mMediaController?.seekTo(position)
    }

    private fun updatePlaybackState(state: Int) {
        _playbackState.value = PlaybackState(
            state = state,
            position = mMediaController?.currentPosition ?: 0L,
            duration = mMediaController?.duration ?: 0L
        )
    }

    // Player.Listener implementation
    override fun onPlaybackStateChanged(playbackState: Int) {
        updatePlaybackState(playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING -> mListener?.showProgressBar()
            Player.STATE_READY -> mListener?.hideProgressBar()
            Player.STATE_ENDED -> updatePlaybackState(PlaybackState.STATE_ENDED)
            Player.STATE_IDLE -> updatePlaybackState(PlaybackState.STATE_IDLE)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            mListener?.hideProgressBar()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        mediaItem?.let {
            mCurrentMediaItem = it
            _currentMetadata.value = it.metadata
            it.metadata?.let { metadata ->
                mListener?.handleMetadataChanged(metadata)
            }
        }
    }

    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
        updatePlaybackState(PlaybackState.STATE_IDLE)
        mListener?.hideProgressBar()
    }

    companion object {
        private const val KEY_LAST_STATION_ID = "last_station_id"
        private const val KEY_SAVE_INSTANCE_PASSED = "save_instance_passed"
    }
}
