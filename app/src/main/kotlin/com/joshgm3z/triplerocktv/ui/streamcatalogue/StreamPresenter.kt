package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardBinding
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class StreamPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewStreamCardBinding.inflate(
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
        val rating = when (item) {
            is StreamData -> item.rating
            else -> null
        }
        val titleView = viewHolder.view.findViewById<TextView>(R.id.stream_title)
        val ratingTv = viewHolder.view.findViewById<TextView>(R.id.tv_rating)
        val ratingContainer = viewHolder.view.findViewById<LinearLayout>(R.id.ll_rating_container)
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)

        rating?.let {
            ratingTv.text = it.toString()
        }
        ratingContainer.visibility = if (rating != null && rating > 0) View.VISIBLE else View.GONE

        titleView.text = title
        glideUtil.loadImage(imageUri, imageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val imageView = viewHolder.view.findViewById<ImageView>(R.id.poster_image)
        Glide.with(imageView.context).clear(imageView)
    }

}
