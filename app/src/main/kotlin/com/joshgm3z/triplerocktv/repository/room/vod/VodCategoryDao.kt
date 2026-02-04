package com.joshgm3z.triplerocktv.repository.room.vod

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VodCategoryDao {
    @Query("SELECT * FROM vod_category")
    fun getAllCategories(): List<VodCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VodCategory)

    @Query("DELETE FROM vod_category")
    suspend fun deleteAllCategories()
}
