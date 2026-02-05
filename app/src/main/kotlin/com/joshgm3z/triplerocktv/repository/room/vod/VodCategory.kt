package com.joshgm3z.triplerocktv.repository.room.vod

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vod_category")
data class VodCategory(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,
    var firstStreamIcon: String? = null,
)