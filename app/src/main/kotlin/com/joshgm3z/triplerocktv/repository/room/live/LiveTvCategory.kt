package com.joshgm3z.triplerocktv.repository.room.live

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "live_tv_category")
data class LiveTvCategory(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,
){
    companion object{
        fun samples(): List<LiveTvCategory> = listOf(
            LiveTvCategory(
                categoryId = 122,
                categoryName = "ENGLISH (4K)",
                parentId = 0,
                count = 3
            ),
            LiveTvCategory(
                categoryId = 56,
                categoryName = "OSCAR WINNING MOVIES",
                parentId = 0,
                count = 2
            ),
            LiveTvCategory(
                categoryId = 43,
                categoryName = "ENGLISH FHD (2026)",
                parentId = 0,
                count = 1
            ),
        )
    }
}
