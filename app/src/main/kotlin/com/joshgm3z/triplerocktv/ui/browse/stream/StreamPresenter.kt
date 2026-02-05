package com.joshgm3z.triplerocktv.ui.browse.stream

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream

class StreamPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
        return ViewHolder(view)
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


        val view = viewHolder.view
        val icon = view.findViewById<ImageView>(R.id.streamIcon)
        val name = view.findViewById<TextView>(R.id.streamName)

        name.text = title
        Glide.with(viewHolder.view)
            .load(imageUri)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(icon)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val icon = viewHolder.view.findViewById<ImageView>(R.id.streamIcon)
        Glide.with(viewHolder.view).clear(icon)
    }
}