package com.joshgm3z.triplerocktv.ui.browse.category

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory

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
            is VodCategory -> item.categoryName
            is SeriesCategory -> item.categoryName
            is LiveTvCategory -> item.categoryName
            else -> "Unknown"
        }
        val cardView = viewHolder.view as CategoryCardView
        cardView.titleView.text = title
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as CategoryCardView
        cardView.iconView.setImageResource(-1)
    }
}