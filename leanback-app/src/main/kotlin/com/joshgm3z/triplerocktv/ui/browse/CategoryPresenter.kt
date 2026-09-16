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
@Inject constructor() : Presenter() {
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
            else -> ""
        }
        val binding = ViewCategoryCardBinding.bind(viewHolder.view)
        binding.tvTitle.text = title
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {

    }
}