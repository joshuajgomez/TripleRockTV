package com.joshgm3z.triplerocktv.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.m3u8.M3uChannel
import com.joshgm3z.triplerocktv.databinding.ViewPlaylistCardBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class PlaylistPresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {
    override fun onCreateViewHolder(viewGroup: ViewGroup): ViewHolder {
        val binding = ViewPlaylistCardBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        item: Any?
    ) {
        val m3uChannel = item as M3uChannel
        val binding = ViewPlaylistCardBinding.bind(holder.view)
        binding.tvProgramName.text = m3uChannel.name
        glideUtil.loadImage(
            m3uChannel.logoUrl,
            binding.ivIcon,
            centerCrop = false,
            error = R.drawable.baseline_ondemand_video_24,
        )
    }

    override fun onUnbindViewHolder(holder: ViewHolder) {}
}