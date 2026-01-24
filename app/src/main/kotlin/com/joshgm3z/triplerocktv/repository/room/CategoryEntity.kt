package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val categoryId: Int,
    val categoryName: String,
    val parentId: Int,
)
