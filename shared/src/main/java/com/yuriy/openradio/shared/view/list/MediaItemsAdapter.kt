package com.yuriy.openradio.shared.view.list

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

open class MediaItemViewHolder(
    view: View,
    val mRoot: View,
    val mNameView: TextView,
    val mDescriptionView: TextView,
    val mImageView: ImageView,
    val mFavoriteView: CheckBox,
    val mBitrateView: TextView,
    val mSettingsView: View?,
    val mForegroundView: View
) {
    val mFavoriteCheckView: CheckBox = mFavoriteView
}

interface OnSettingsListener {
    fun onSettingsClick(item: MediaItem)
}

open class MediaItemsAdapter {
    var activeItemId: Int = -1
    var parentId: String = ""

    open fun getItem(position: Int): MediaItem? = null

    open fun updateImage(context: Context, metadata: MediaMetadata, imageView: ImageView) {
    }

    open fun updateBitrateView(bitrate: Int, bitrateView: TextView, isPlayable: Boolean) {
    }

    open fun handleNameAndDescriptionView(nameView: TextView, descriptionView: TextView, metadata: MediaMetadata, parentId: String) {
    }

    open fun handleFavoriteAction(
        checkBox: CheckBox,
        mediaId: String,
        metadata: MediaMetadata,
        commander: Any?
    ) {
    }

    open fun onViewRecycled(holder: MediaItemViewHolder) {
    }

    open fun onCreateViewHolder(parent: Any, viewType: Int): MediaItemViewHolder {
        return MediaItemViewHolder(
            View(parent as Context),
            View(parent),
            TextView(parent),
            TextView(parent),
            ImageView(parent),
            CheckBox(parent),
            TextView(parent),
            null,
            View(parent)
        )
    }

    open fun onBindViewHolder(holder: MediaItemViewHolder, position: Int) {
    }

    open fun getItemCount(): Int {
        return 0
    }

    var listener: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemSelected(item: MediaItem, position: Int)
    }
}
