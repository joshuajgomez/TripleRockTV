package com.joshgm3z.triplerocktv.ui.browse.recents

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewRecentStreamCardBinding
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class RecentStreamPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewRecentStreamCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val title = when (item) {
            is StreamData -> item.name
            is Episode -> item.title
            else -> "Unknown"
        }
        val imageUri = when (item) {
            is StreamData -> item.streamIcon
            is Episode -> item.episodeInfo?.movie_image
            else -> "Unknown"
        }
        val progress = when (item) {
            is StreamData -> item.progressPercent()
            is Episode -> item.progressPercent()
            else -> 0
        }
        ViewRecentStreamCardBinding.bind(viewHolder.view).apply {
            streamTitle.text = title
            glideUtil.loadImage(imageUri, posterImage)
            progressBar.progress = progress
            progressBar.visibility = if (progress > 0) ProgressBar.VISIBLE else ProgressBar.GONE
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)
        Glide.with(imageView.context).clear(imageView)
    }
}