package com.joshgm3z.triplerocktv.core.repository.room.category

import androidx.room.Entity
import com.joshgm3z.triplerocktv.core.repository.StreamType

@Entity(
    tableName = "category_data",
    primaryKeys = ["categoryId", "streamType"]
)
data class CategoryData(
    val categoryId: Int = 0,
    val categoryName: String = "",
    val parentId: Int = 0,
    var count: Int = 0,

    var firstStreamIcon: String? = null,
    var streamType: StreamType,
)