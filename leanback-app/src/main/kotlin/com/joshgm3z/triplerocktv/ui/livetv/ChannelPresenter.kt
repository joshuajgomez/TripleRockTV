package com.joshgm3z.triplerocktv.ui.livetv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.databinding.ViewProgramCardBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class ChannelPresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewProgramCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        item: Any?
    ) {
        val streamData = item as? StreamData ?: return
        val binding = ViewProgramCardBinding.bind(viewHolder.view)

        binding.tvProgramName.text = streamData.name
        glideUtil.loadImage(streamData.streamIcon, binding.ivLogo)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewProgramCardBinding.bind(viewHolder.view)
        binding.ivLogo.setImageDrawable(null)
    }
}