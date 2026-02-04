package com.joshgm3z.triplerocktv.ui.search

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

class SearchPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Set dimensions for the card
            setMainImageDimensions(300, 150)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val stream = item as StreamEntity
        val cardView = viewHolder.view as ImageCardView

        // Set the title/text
        cardView.titleText = stream.name

        // Load the image using Glide
        // Assuming StreamEntity has an 'imageUrl' or 'thumbnail' field
        Glide.with(cardView.context)
            .load(stream.streamIcon) // Replace with your actual field name
            .centerCrop()
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        // Clear Glide request to free resources
        Glide.with(cardView.context).clear(cardView.mainImageView)
        cardView.mainImage = null
    }
}