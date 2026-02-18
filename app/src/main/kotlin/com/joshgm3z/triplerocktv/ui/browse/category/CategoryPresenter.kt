package com.joshgm3z.triplerocktv.ui.browse.category

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory

class CategoryPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = CategoryCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val title = when (item) {
            is CategoryData -> item.categoryName
            is SeriesCategory -> item.categoryName
            else -> "Unknown"
        }
        val streamIcon = when (item) {
            is CategoryData -> item.firstStreamIcon
            is SeriesCategory -> item.firstStreamIcon
            else -> null
        }
        val count = when (item) {
            is CategoryData -> item.count
            is SeriesCategory -> item.count
            else -> 0
        }
        val cardView = viewHolder.view as CategoryCardView
        cardView.titleView.text = title
        cardView.countView.text = "$count videos"
        streamIcon?.let {
            Glide.with(cardView)
                .load(it)
                .into(cardView.imageView)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as CategoryCardView
        cardView.iconView.setImageResource(-1)
        Glide.with(cardView)
            .clear(cardView.imageView)
    }
}