package com.joshgm3z.triplerocktv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

class StreamPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // Inflate your item layout here
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val stream = item as StreamEntity
        // Bind your data to the view
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}