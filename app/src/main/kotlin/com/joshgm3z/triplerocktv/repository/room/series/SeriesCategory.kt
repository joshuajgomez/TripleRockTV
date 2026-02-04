package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series_category")
data class SeriesCategory(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
    var count: Int = 0,
){
    companion object{
        fun samples(): List<SeriesCategory> = listOf(
            SeriesCategory(
                categoryId = 122,
                categoryName = "ENGLISH (4K)",
                parentId = 0,
                count = 3
            ),
            SeriesCategory(
                categoryId = 56,
                categoryName = "OSCAR WINNING MOVIES",
                parentId = 0,
                count = 2
            ),
            SeriesCategory(
                categoryId = 43,
                categoryName = "ENGLISH FHD (2026)",
                parentId = 0,
                count = 1
            ),
        )
    }
}
