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

        cardView.titleText = title
        Glide.with(cardView.context)
            .load(imageUri) // Replace with your actual field name
            .placeholder(R.drawable.ic_video_file)
            .centerCrop()
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        Glide.with(cardView.context).clear(cardView.mainImageView)
        cardView.mainImage = null
    }
}