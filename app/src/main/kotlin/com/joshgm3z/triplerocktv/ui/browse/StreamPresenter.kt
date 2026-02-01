package com.joshgm3z.triplerocktv.ui.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

class StreamPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val stream = item as StreamEntity
        val view = viewHolder.view
        val icon = view.findViewById<ImageView>(R.id.streamIcon)
        val name = view.findViewById<TextView>(R.id.streamName)

        name.text = stream.name
        Glide.with(viewHolder.view)
            .load(stream.streamIcon)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(icon)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val icon = viewHolder.view.findViewById<ImageView>(R.id.streamIcon)
        icon.setImageDrawable(null)
    }
}