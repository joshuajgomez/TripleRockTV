package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series_category")
data class SeriesCategory(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,
    var firstStreamIcon: String? = null,
)