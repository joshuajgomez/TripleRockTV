package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesStreamsDao {

    @Query("SELECT * FROM series_stream WHERE name LIKE :streamName")
    fun searchStreams(streamName: String): List<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE seriesId = :seriesId")
    fun getStream(seriesId: Int): Flow<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE categoryId = :categoryId")
    fun getAllStreams(categoryId: Int): List<SeriesStream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<SeriesStream>)

    @Query("DELETE FROM series_stream")
    suspend fun deleteAllStreams()
}
