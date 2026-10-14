package com.joshgm3z.triplerocktv.ui.playlist.main

import android.view.LayoutInflater
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.playlist.Playlist
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistType
import com.joshgm3z.triplerocktv.core.util.relativeTime
import com.joshgm3z.triplerocktv.core.util.toTextTime
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
        val playlist = item as Playlist
        binding.tvTitle.text = playlist.playlistName
        binding.tvSubtitle.text = "added ${playlist.added.relativeTime()}"

        binding.tvTypeChip.text = playlist.type.name.uppercase()
        binding.tvTypeChip.setBackgroundResource(
            if (playlist.type == PlaylistType.Xtream) R.drawable.xtream_chip
            else R.drawable.m3u8_chip
        )
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources
    }
}