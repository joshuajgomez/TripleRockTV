package com.joshgm3z.triplerocktv.ui.browse.recents

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardShortBinding
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream

class RecentStreamPresenter() : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewStreamCardShortBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val title = when (item) {
            is StreamData -> item.name
            is SeriesStream -> item.name
            else -> "Unknown"
        }
        val imageUri = when (item) {
            is StreamData -> item.streamIcon
            is SeriesStream -> item.cover
            else -> "Unknown"
        }
        val progress = when (item) {
            is StreamData -> item.progressPercent()
            else -> 0
        }
        val titleView = viewHolder.view.findViewById<TextView>(R.id.stream_title)
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)
        val progressBar = viewHolder.view.findViewById<ProgressBar>(R.id.progress_bar)

        titleView.text = title
        Glide.with(imageView.context)
            .load(imageUri) // Replace with your actual field name
            .centerCrop()
            .into(imageView)
        progressBar.progress = progress
        progressBar.visibility = if (progress > 0) ProgressBar.VISIBLE else ProgressBar.GONE
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)
        Glide.with(imageView.context).clear(imageView)
    }
}