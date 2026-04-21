package com.yuriy.openradio.mobile.view.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuriy.openradio.mobile.R
import com.yuriy.openradio.shared.model.media.RadioStation
import com.yuriy.openradio.shared.view.list.MediaItemViewHolder

/**
 * Mobile media items adapter with selection and playing state support.
 * Extends ListAdapter for efficient diff-based updates.
 */
class MobileMediaItemsAdapter(
    private val onItemClick: (RadioStation) -> Unit,
    private val onFavoriteClick: (RadioStation, Boolean) -> Unit
) : ListAdapter<RadioStation, MobileMediaItemsAdapter.StationViewHolder>(StationDiffCallback()) {

    private var mActiveItemId: String? = null
    private var mPlayingItemId: String? = null

    /**
     * Sets the currently selected/active item ID.
     */
    fun setActiveItemId(itemId: String?) {
        val oldId = mActiveItemId
        mActiveItemId = itemId
        // Refresh old and new active items
        currentList.forEachIndexed { index, station ->
            if (station.id == oldId || station.id == itemId) {
                notifyItemChanged(index)
            }
        }
    }

    /**
     * Sets the currently playing item ID.
     */
    fun setPlayingItemId(itemId: String?) {
        val oldId = mPlayingItemId
        mPlayingItemId = itemId
        // Refresh old and new playing items
        currentList.forEachIndexed { index, station ->
            if (station.id == oldId || station.id == itemId) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = getItem(position)
        holder.bind(station, station.id == mActiveItemId, station.id == mPlayingItemId)
    }

    override fun onViewRecycled(holder: StationViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    /**
     * ViewHolder for station items with proper state management.
     */
    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mRoot: View = itemView.findViewById(R.id.item_root)
        private val mNameView: TextView = itemView.findViewById(R.id.station_name)
        private val mDescriptionView: TextView = itemView.findViewById(R.id.station_description)
        private val mImageView: ImageView = itemView.findViewById(R.id.station_image)
        private val mFavoriteView: CheckBox = itemView.findViewById(R.id.station_favorite)
        private val mBitrateView: TextView = itemView.findViewById(R.id.station_bitrate)
        private val mPlayingIndicator: View = itemView.findViewById(R.id.playing_indicator)

        fun bind(station: RadioStation, isActive: Boolean, isPlaying: Boolean) {
            mNameView.text = station.name
            mDescriptionView.text = buildDescription(station)
            mBitrateView.text = formatBitrate(station.mediaStream.bitRate)

            // Set favorite state
            mFavoriteView.isChecked = station.status == RadioStation.STATUS_FAVORITE

            // Handle active state (selection)
            mRoot.isSelected = isActive
            mRoot.alpha = if (isActive) 1.0f else 0.87f

            // Handle playing state (indicator)
            mPlayingIndicator.visibility = if (isPlaying) View.VISIBLE else View.GONE

            // Load station image if available
            loadStationImage(station.imageUrl)

            // Click listeners
            itemView.setOnClickListener {
                onItemClick(station)
            }

            mFavoriteView.setOnClickListener { view ->
                if (view is CheckBox) {
                    onFavoriteClick(station, view.isChecked)
                }
            }
        }

        fun recycle() {
            // Clean up resources if needed
            mImageView.setImageDrawable(null)
        }

        private fun buildDescription(station: RadioStation): String {
            val parts = mutableListOf<String>()
            if (station.genre.isNotEmpty()) {
                parts.add(station.genre)
            }
            if (station.countryCode.isNotEmpty()) {
                parts.add(station.countryCode)
            }
            return parts.joinToString(" • ")
        }

        private fun formatBitrate(bitRate: Int): String {
            return if (bitRate > 0) "${bitRate}kbps" else ""
        }

        private fun loadStationImage(imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                // Use an image loading library like Glide or Coil
                // For now, just set a placeholder
                mImageView.setImageResource(R.drawable.ic_radio)
            } else {
                mImageView.setImageResource(R.drawable.ic_radio)
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    class StationDiffCallback : DiffUtil.ItemCallback<RadioStation>() {
        override fun areItemsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.name == newItem.name &&
                    oldItem.status == newItem.status &&
                    oldItem.imageUrl == newItem.imageUrl &&
                    oldItem.mediaStream.bitRate == newItem.mediaStream.bitRate
        }
    }
}
