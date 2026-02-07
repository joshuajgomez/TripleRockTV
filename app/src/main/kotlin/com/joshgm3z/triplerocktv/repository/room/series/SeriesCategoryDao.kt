package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SeriesCategoryDao {
    @Query("SELECT * FROM series_category")
    fun getAllCategories(): List<SeriesCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seriesCategory: SeriesCategory)

    @Query("DELETE FROM series_category")
    suspend fun deleteAllCategories()
}
