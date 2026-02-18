package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joshgm3z.triplerocktv.repository.StreamType

@Dao
interface CategoryDataDao {
    @Query("SELECT * FROM category_data WHERE streamType = :streamType")
    fun getAllOfType(streamType: StreamType): List<CategoryData>

    @Query("SELECT * FROM category_data")
    fun getAll(): List<CategoryData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryData: CategoryData)

    @Query("DELETE FROM category_data")
    suspend fun deleteAll()

    @Query("DELETE FROM category_data WHERE streamType = :streamType")
    suspend fun deleteAllOfType(streamType: StreamType)
}
