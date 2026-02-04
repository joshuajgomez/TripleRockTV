package com.joshgm3z.triplerocktv.repository.room.live

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LiveTvCategoryDao {
    @Query("SELECT * FROM live_tv_category")
    fun getAllCategories(): Flow<List<LiveTvCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(liveCategory: LiveTvCategory)

    @Query("DELETE FROM live_tv_category")
    suspend fun deleteAllCategories()
}
