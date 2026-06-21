package com.joshgm3z.triplerocktv.ui.livetv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.databinding.ViewChannelBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import javax.inject.Inject

class ChannelPresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {

    var setFavorite: ((StreamData, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewChannelBinding.inflate(
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
        val binding = ViewChannelBinding.bind(viewHolder.view)

        binding.tvProgramName.text = streamData.name
        binding.ivStar.setVisible(streamData.inMyList)
        binding.root.setOnLongClickListener {
            setFavorite?.invoke(streamData, !binding.ivStar.isVisible)
            binding.ivStar.setVisible(!binding.ivStar.isVisible)
            true
        }
        glideUtil.loadImage(
            streamData.streamIcon,
            binding.ivLogo,
            error = R.drawable.baseline_ondemand_video_24
        )
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewChannelBinding.bind(viewHolder.view)
        binding.ivLogo.setImageDrawable(null)
    }
}