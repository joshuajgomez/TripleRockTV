package com.joshgm3z.triplerocktv.ui.playlist.main

import android.view.LayoutInflater
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewPlaylistCardBinding

class PlaylistPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val binding = ViewPlaylistCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = ViewPlaylistCardBinding.bind(viewHolder.view)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources
    }
}