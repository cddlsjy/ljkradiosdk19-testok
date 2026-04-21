package com.yuriy.openradio.mobile.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yuriy.openradio.mobile.R
import com.yuriy.openradio.mobile.dependencies.DependencyRegistry
import com.yuriy.openradio.mobile.view.list.MobileMediaItemsAdapter
import com.yuriy.openradio.shared.broadcast.AppLocalReceiverCallback
import com.yuriy.openradio.shared.dependencies.DependencyRegistryCommon
import com.yuriy.openradio.shared.media.PlaybackState
import com.yuriy.openradio.shared.model.media.MediaItemsSubscription
import com.yuriy.openradio.shared.model.media.RadioStation
import com.yuriy.openradio.shared.presenter.MediaPresenter
import com.yuriy.openradio.shared.presenter.MediaPresenterListener
import com.yuriy.openradio.shared.service.location.Country
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Main activity for mobile (non-TV) OpenRadio app.
 * Handles UI setup, permission requests, and playback control.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: MobileMediaItemsAdapter
    private lateinit var mMediaPresenter: MediaPresenter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mFab: FloatingActionButton

    private var mStations = mutableListOf<RadioStation>()
    private var mSelectedPosition = -1

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            loadStations()
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DependencyRegistry.inject(this)
        DependencyRegistryCommon.init(this)

        initViews()
        initPresenter()
        checkPermissions()
    }

    private fun initViews() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mProgressBar = findViewById(R.id.progress_bar)
        mFab = findViewById(R.id.fab)

        // Setup RecyclerView
        mAdapter = MobileMediaItemsAdapter(
            onItemClick = { station -> onStationClicked(station) },
            onFavoriteClick = { station, isFavorite -> onFavoriteClicked(station, isFavorite) }
        )
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        // Setup FAB click listener
        mFab.setOnClickListener { showAddStationDialog() }

        // Handle item selection for D-pad navigation
        mRecyclerView.setOnClickListener { view ->
            val position = mRecyclerView.getChildAdapterPosition(view)
            if (position != RecyclerView.NO_POSITION) {
                handleItemSelection(position)
            }
        }
    }

    private fun initPresenter() {
        mMediaPresenter = DependencyRegistryCommon.getMediaPresenter()

        mMediaPresenter.init(
            this,
            findViewById<View>(R.id.main_container),
            savedInstanceState,
            mRecyclerView,
            findViewById<View>(R.id.current_station_view),
            mAdapter,
            object : MediaItemsSubscription {
                override fun onChildrenLoaded(parentId: String, children: List<MediaItem>) {
                    runOnUiThread { /* Handle loaded children */ }
                }
                override fun onError(parentId: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, R.string.error_loading, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            object : MediaPresenterListener {
                override fun showProgressBar() {
                    runOnUiThread { mProgressBar.visibility = View.VISIBLE }
                }
                override fun handleMetadataChanged(metadata: MediaMetadata) {
                    runOnUiThread { updateCurrentStation(metadata) }
                }
                override fun handlePlaybackStateChanged(state: PlaybackState) {
                    runOnUiThread { handlePlaybackState(state) }
                }
            },
            object : AppLocalReceiverCallback {
                override fun onCurrentIndexOnQueueChanged(index: Int) {
                    runOnUiThread { mSelectedPosition = index }
                }
            }
        )

        // Observe playback state via Flow
        lifecycleScope.launch {
            mMediaPresenter.playbackState.collectLatest { state ->
                handlePlaybackState(state)
            }
        }

        // Observe current metadata via Flow
        lifecycleScope.launch {
            mMediaPresenter.currentMetadata.collectLatest { metadata ->
                metadata?.let { updateCurrentStation(it) }
            }
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isEmpty()) {
            loadStations()
        } else {
            requestPermissionLauncher.launch(notGranted.toTypedArray())
        }
    }

    private fun loadStations() {
        // Load stations from storage
        val storage = DependencyRegistryCommon.getRadioStationsStorage(Country.getCountryCode())
        mStations.clear()
        mStations.addAll(storage.getAll())

        // Add some sample stations if empty (for demonstration)
        if (mStations.isEmpty()) {
            addSampleStations()
        }

        mAdapter.submitList(mStations.toList())
    }

    private fun addSampleStations() {
        // Sample radio stations for demonstration
        val sampleStations = listOf(
            RadioStation().apply {
                id = "1"
                name = "Pop Radio"
                genre = "Pop"
                countryCode = "US"
                mediaStream.bitRate = 128
                mediaStream.streamUrl = "http://stream.example.com/pop"
            },
            RadioStation().apply {
                id = "2"
                name = "Rock FM"
                genre = "Rock"
                countryCode = "UK"
                mediaStream.bitRate = 192
                mediaStream.streamUrl = "http://stream.example.com/rock"
            },
            RadioStation().apply {
                id = "3"
                name = "Classical Music"
                genre = "Classical"
                countryCode = "DE"
                mediaStream.bitRate = 256
                mediaStream.streamUrl = "http://stream.example.com/classical"
            }
        )
        mStations.addAll(sampleStations)
    }

    private fun onStationClicked(station: RadioStation) {
        val position = mStations.indexOf(station)
        if (position != -1) {
            handleItemSelection(position)
            playStation(station)
        }
    }

    private fun handleItemSelection(position: Int) {
        if (position < 0 || position >= mStations.size) return

        mSelectedPosition = position
        mAdapter.setActiveItemId(mStations[position].id)

        // Scroll to position
        mRecyclerView.smoothScrollToPosition(position)
    }

    private fun playStation(station: RadioStation) {
        val mediaItem = MediaItem.Builder()
            .setUri(station.mediaStream.streamUrl)
            .setMediaId(station.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist(station.genre)
                    .build()
            )
            .build()

        mMediaPresenter.play(mediaItem)
        mAdapter.setPlayingItemId(station.id)

        // Save last played station
        val latestStorage = DependencyRegistryCommon.getLatestRadioStationStorage()
        latestStorage.setLastStationId(station.id)
    }

    private fun onFavoriteClicked(station: RadioStation, isFavorite: Boolean) {
        station.status = if (isFavorite) RadioStation.STATUS_FAVORITE else RadioStation.STATUS_DEFAULT

        // Update storage
        val favoritesStorage = DependencyRegistryCommon.getFavoritesStorage()
        if (isFavorite) {
            favoritesStorage.add(station)
        } else {
            favoritesStorage.remove(station.id)
        }

        // Update list
        val position = mStations.indexOf(station)
        if (position != -1) {
            mAdapter.notifyItemChanged(position)
        }
    }

    private fun handlePlaybackState(state: PlaybackState) {
        when (state.state) {
            PlaybackState.STATE_BUFFERING -> {
                mProgressBar.visibility = View.VISIBLE
                mFab.setImageResource(R.drawable.ic_pause)
            }
            PlaybackState.STATE_READY -> {
                mProgressBar.visibility = View.GONE
                mFab.setImageResource(R.drawable.ic_pause)
            }
            PlaybackState.STATE_IDLE, PlaybackState.STATE_ENDED -> {
                mProgressBar.visibility = View.GONE
                mFab.setImageResource(R.drawable.ic_play_arrow)
                mAdapter.setPlayingItemId(null)
            }
        }
    }

    private fun updateCurrentStation(metadata: MediaMetadata) {
        // Update the current station view with metadata
        val titleView = findViewById<android.widget.TextView>(R.id.current_station_name)
        titleView?.text = metadata.title ?: ""
    }

    private fun showAddStationDialog() {
        // Show add station dialog
        Toast.makeText(this, R.string.add_station, Toast.LENGTH_SHORT).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (mSelectedPosition > 0) {
                    handleItemSelection(mSelectedPosition - 1)
                }
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (mSelectedPosition < mStations.size - 1) {
                    handleItemSelection(mSelectedPosition + 1)
                }
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if (mSelectedPosition >= 0 && mSelectedPosition < mStations.size) {
                    playStation(mStations[mSelectedPosition])
                }
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                val currentItem = mMediaPresenter.getCurrentMediaItem()
                if (currentItem != null) {
                    // Toggle play/pause
                    mMediaPresenter.pause()
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onResume() {
        super.onResume()
        mMediaPresenter.handleResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMediaPresenter.handleSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_POSITION, mSelectedPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPresenter.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mMediaPresenter.handlePermissionsResult(permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            loadStations()
        }
    }

    companion object {
        private const val KEY_SELECTED_POSITION = "selected_position"
    }
}
