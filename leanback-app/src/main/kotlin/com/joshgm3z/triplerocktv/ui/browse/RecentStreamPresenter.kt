package com.joshgm3z.triplerocktv.ui.browse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.databinding.ViewRecentStreamCardBinding
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.ifNullOrEmpty
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class RecentStreamPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {

    var longPressListener: ((
        streamId: Int,
        streamType: StreamType,
        anchorView: View
    ) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewRecentStreamCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = ViewRecentStreamCardBinding.bind(viewHolder.view)
        if (item is Int && item == emptyItem) {
            binding.tvResume.setVisible(false)
            binding.llDarkOverlay.setVisible(false)
            binding.tvRecentEmptyText.setVisible(true)
            val color = binding.root.context.getColor(
                com.joshgm3z.triplerocktv.core.R.color.color_foreground_low
            )
            binding.streamTitle.setTextColor(color)
            binding.ivIcon.setVisible(false)
            binding.root.focusable = View.NOT_FOCUSABLE
            return
        }

        val title = when (item) {
            is StreamData -> item.name
            is Episode -> item.title
            else -> "Unknown"
        }
        val imageUri = when (item) {
            is StreamData -> item.movieMetadata?.backPosterUrl.ifNullOrEmpty(item.streamIcon)
            is Episode -> item.episodeInfo?.movie_image
            else -> "Unknown"
        }
        val progress = when (item) {
            is StreamData -> item.progressPercent()
            is Episode -> item.progressPercent()
            else -> 0
        }
        val streamType = when (item) {
            is StreamData -> item.streamType
            is Episode -> StreamType.Series
            else -> null
        }
        binding.apply {
            streamTitle.text = title
            if (streamType == StreamType.LiveTV) glideUtil.loadImage(
                url = imageUri,
                imageView = ivIcon,
                centerCrop = false,
                error = R.drawable.ic_video_file
            )
            else glideUtil.loadImage(imageUri, posterImage)
            progressBar.progress = progress ?: 0
            progressBar.setVisible(progress != null)

            root.setOnLongClickListener {
                when (item) {
                    is StreamData -> longPressListener?.invoke(
                        item.streamId,
                        item.streamType,
                        root
                    )

                    is Episode -> longPressListener?.invoke(
                        item.id,
                        StreamType.Series,
                        root
                    )
                }
                true
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewRecentStreamCardBinding.bind(viewHolder.view)
        Glide.with(binding.root.context).clear(binding.posterImage)
        binding.progressBar.setVisible(false)
    }
}
