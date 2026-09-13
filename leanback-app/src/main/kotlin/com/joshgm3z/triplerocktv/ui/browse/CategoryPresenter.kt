package com.joshgm3z.triplerocktv.ui.browse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.databinding.ViewCategoryCardBinding
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.withComma
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class CategoryPresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewCategoryCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val title = when (item) {
            is CategoryData -> item.categoryName
            else -> "Unknown"
        }
        val streamIcon = when (item) {
            is CategoryData -> item.firstStreamIcon
            else -> null
        }
        val count = when (item) {
            is CategoryData -> item.count
            else -> 0
        }
        val streamType = when (item) {
            is CategoryData -> item.streamType
            else -> null
        }
        val binding = ViewCategoryCardBinding.bind(viewHolder.view)
        binding.tvTitle.text = title
        binding.tvCount.text = "${count.withComma()} videos"

        when (streamType) {
            StreamType.LiveTV -> glideUtil.loadImage(
                streamIcon,
                binding.ivIcon,
                error = R.drawable.ic_video_file,
                onSuccess = {
                    binding.ivIcon.imageTintList = null
                }
            )

            else -> glideUtil.loadImage(
                streamIcon,
                binding.ivPoster
            )
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewCategoryCardBinding.bind(viewHolder.view)
        Glide.with(binding.root.context).clear(binding.ivPoster)
        Glide.with(binding.root.context).clear(binding.ivIcon)
        binding.ivIcon.imageTintList = binding.root.context.getColorStateList(
            com.joshgm3z.triplerocktv.core.R.color.color_foreground_high_selector
        )
        binding.ivIcon.setImageResource(R.drawable.ic_video_file)
    }
}