package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joshgm3z.triplerocktv.repository.StreamType

@Entity(tableName = "category_data")
data class CategoryData(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,

    var firstStreamIcon: String? = null,
    var streamType: StreamType,
)