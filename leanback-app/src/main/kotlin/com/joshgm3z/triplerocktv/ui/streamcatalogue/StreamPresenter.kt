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
import com.joshgm3z.triplerocktv.core.util.isRecent
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class StreamPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {

    var longPressListener: ((
        streamId: Int,
        streamType: StreamType,
        anchorView: View
    ) -> Unit)? = null

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

        when (item) {
            is StreamData -> item.name
            is SeriesStream -> item.name
            else -> null
        }.let {
            binding.streamTitle.text = it
        }

        when (item) {
            is StreamData -> when (item.streamType) {
                StreamType.LiveTV -> glideUtil.loadImage(
                    item.streamIcon,
                    binding.ivIcon,
                    centerCrop = false,
                    error = R.drawable.ic_video_file
                )

                else -> glideUtil.loadImage(
                    item.streamIcon,
                    binding.posterImage
                )
            }

            is SeriesStream -> glideUtil.loadImage(
                item.coverImageUrl,
                binding.posterImage
            )
        }

        when (item) {
            is StreamData -> item.added.isRecent()
            is SeriesStream -> item.lastModified.isRecent()
            else -> null
        }.let {
            binding.tvNew.setVisible(it)
        }

        binding.root.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.ivGo.setVisible(hasFocus)
        }
        binding.root.setOnLongClickListener {
            when (item) {
                is StreamData -> longPressListener?.invoke(
                    item.streamId,
                    item.streamType,
                    binding.root
                )

                is SeriesStream -> longPressListener?.invoke(
                    item.seriesId,
                    StreamType.Series,
                    binding.root
                )
            }
            true
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewStreamCardBinding.bind(viewHolder.view)
        Glide.with(binding.root.context).clear(binding.posterImage)
        Glide.with(binding.root.context).clear(binding.ivIcon)
        binding.ivIcon.setImageResource(R.drawable.ic_video_file)
    }

}
