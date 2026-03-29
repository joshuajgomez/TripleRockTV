package com.joshgm3z.triplerocktv.ui.browse.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewCategoryCardBinding
import com.joshgm3z.triplerocktv.repository.room.CategoryData
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

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
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
        val binding = ViewCategoryCardBinding.bind(viewHolder.view)
        binding.tvTitle.text = title
        binding.tvCount.text = "$count videos"
        glideUtil.loadImage(streamIcon, binding.ivPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {

    }
}