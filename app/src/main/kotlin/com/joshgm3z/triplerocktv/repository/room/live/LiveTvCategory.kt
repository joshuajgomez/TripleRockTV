package com.joshgm3z.triplerocktv.repository.room.live

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "live_tv_category")
data class LiveTvCategory(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,
)