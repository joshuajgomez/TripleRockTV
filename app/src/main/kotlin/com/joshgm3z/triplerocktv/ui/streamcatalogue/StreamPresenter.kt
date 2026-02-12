package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardBinding
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardShortBinding
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream

class StreamPresenter(
    val isShortCard: Boolean = false
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = when {
            isShortCard -> ViewStreamCardShortBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            else -> ViewStreamCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        }
        return ViewHolder(binding.root)
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
        val titleView = viewHolder.view.findViewById<TextView>(R.id.stream_title)
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)

        titleView.text = title
        Glide.with(imageView.context)
            .load(imageUri) // Replace with your actual field name
            .centerCrop()
            .into(imageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)
        Glide.with(imageView.context).clear(imageView)
    }
}