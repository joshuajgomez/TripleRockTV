package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Entity
import com.joshgm3z.triplerocktv.repository.StreamType

@Entity(
    tableName = "category_data",
    primaryKeys = ["categoryId", "streamType"]
)
data class CategoryData(
    val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,

    var firstStreamIcon: String? = null,
    var streamType: StreamType,
)