package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardBinding
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.isRecent
import com.joshgm3z.triplerocktv.ui.browse.emptyItem
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class StreamPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {

    var launchSearchFragment: ((String) -> Unit)? = null

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
        if (item is Int && item == emptyItem) {
            binding.streamTitle.text = "Add favorites to see it listed here"
            val color = binding.root.context.getColor(
                com.joshgm3z.triplerocktv.core.R.color.color_foreground_low
            )
            binding.streamTitle.setTextColor(color)
            binding.ivIcon.setVisible(false)
            binding.root.focusable = View.NOT_FOCUSABLE
            return
        }

        when (item) {
            is StreamData -> item.name
            is SeriesStream -> item.name
            else -> null
        }.let {
            binding.streamTitle.text = it
            binding.tvOverlayStreamTitle.text = it
        }

        when (item) {
            is StreamData -> item.categoryName
            is SeriesStream -> item.categoryName
            else -> null
        }?.let {
            binding.tvOverlayStreamSubtitle.text = it
            binding.tvOverlayStreamSubtitle.setVisible(true)
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
            binding.llOverlay.setVisible(hasFocus)
            binding.streamTitle.setVisible(!hasFocus)
        }
        binding.root.setOnLongClickListener {
            when (item) {
                is StreamData -> showPopupMenu(
                    item.streamId,
                    item.name,
                    item.streamType,
                    binding.root
                )

                is SeriesStream -> showPopupMenu(
                    item.seriesId,
                    item.name,
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

    private fun showPopupMenu(
        streamId: Int,
        streamName: String,
        streamType: StreamType,
        anchorView: View
    ) {
        val popup = PopupMenu(anchorView.context, anchorView)

        val searchSimilar = "Search similar titles"
        popup.menu.add(searchSimilar)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                searchSimilar -> {
                    launchSearchFragment?.invoke(streamName)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }
}
