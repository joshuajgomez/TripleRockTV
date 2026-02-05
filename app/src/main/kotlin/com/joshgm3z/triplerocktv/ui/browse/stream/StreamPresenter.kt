package com.joshgm3z.triplerocktv.ui.browse.stream

import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream

class StreamPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Set dimensions for the card
            setMainImageDimensions(300, 150)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val title = when (item) {
            is VodStream -> item.name
            is LiveTvStream -> item.name
            is SeriesStream -> item.name
            else -> "Unknown"
        }
        val imageUri = when (item) {
            is VodStream -> item.streamIcon
            is LiveTvStream -> item.streamIcon
            is SeriesStream -> item.cover
            else -> "Unknown"
        }


        val cardView = viewHolder.view as ImageCardView

        // Set the title/text
        cardView.titleText = title

        // Load the image using Glide
        // Assuming StreamEntity has an 'imageUrl' or 'thumbnail' field
        Glide.with(cardView.context)
            .load(imageUri) // Replace with your actual field name
            .centerCrop()
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val icon = viewHolder.view.findViewById<ImageView>(R.id.streamIcon)
        Glide.with(viewHolder.view).clear(icon)
    }
}