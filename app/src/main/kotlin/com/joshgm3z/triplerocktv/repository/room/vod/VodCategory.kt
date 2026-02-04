package com.joshgm3z.triplerocktv.repository.room.vod

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vod_category")
data class VodCategory(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,
){
    companion object{
        fun samples(): List<VodCategory> = listOf(
            VodCategory(
                categoryId = 122,
                categoryName = "ENGLISH (4K)",
                parentId = 0,
                count = 3
            ),
            VodCategory(
                categoryId = 56,
                categoryName = "OSCAR WINNING MOVIES",
                parentId = 0,
                count = 2
            ),
            VodCategory(
                categoryId = 43,
                categoryName = "ENGLISH FHD (2026)",
                parentId = 0,
                count = 1
            ),
        )
    }
}
