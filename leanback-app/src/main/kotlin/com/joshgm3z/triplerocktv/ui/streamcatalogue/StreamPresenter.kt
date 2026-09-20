package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardBinding
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.setVisible
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
        val binding = ViewStreamCardBinding.bind(viewHolder.view)
        val title = when (item) {
            is StreamData -> item.name
            is SeriesStream -> item.name
            else -> null
        }
        val imageUri = when (item) {
            is StreamData -> item.streamIcon
            is SeriesStream -> item.coverImageUrl
            else -> null
        }
        val streamType = when (item) {
            is StreamData -> item.streamType
            is SeriesStream -> StreamType.Series
            else -> null
        }

        binding.streamTitle.text = title

        when (streamType) {
            StreamType.LiveTV -> glideUtil.loadImage(
                imageUri,
                binding.ivIcon,
                centerCrop = false,
                error = R.drawable.ic_video_file
            )

            else -> glideUtil.loadImage(
                imageUri,
                binding.posterImage
            )
        }

        binding.root.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.ivGo.setVisible(hasFocus)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewStreamCardBinding.bind(viewHolder.view)
        Glide.with(binding.root.context).clear(binding.posterImage)
        Glide.with(binding.root.context).clear(binding.ivIcon)
        binding.ivIcon.setImageResource(R.drawable.ic_video_file)
    }

}
