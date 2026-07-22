package com.joshgm3z.triplerocktv.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData

val diffCallback = object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
        if (oldItem is StreamData && newItem is StreamData) {
            oldItem.streamId == newItem.streamId
        } else if (oldItem is SeriesStream && newItem is SeriesStream) {
            oldItem.seriesId == newItem.seriesId
        } else false

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
        if (oldItem is StreamData && newItem is StreamData) {
            oldItem.streamId == newItem.streamId
        } else if (oldItem is SeriesStream && newItem is SeriesStream) {
            oldItem.seriesId == newItem.seriesId
        } else false
}

val diffCategoryCallback = object : DiffUtil.ItemCallback<CategoryData>() {
    override fun areItemsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean =
        oldItem.categoryId == newItem.categoryId
}