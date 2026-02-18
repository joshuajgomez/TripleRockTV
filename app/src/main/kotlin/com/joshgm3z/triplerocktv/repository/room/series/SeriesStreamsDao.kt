package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SeriesStreamsDao {

    @Query("SELECT * FROM series_stream WHERE name LIKE '%' || :streamName || '%'")
    fun searchStreams(streamName: String): List<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE seriesId = :seriesId")
    fun getStream(seriesId: Int): SeriesStream

    @Query("SELECT * FROM series_stream WHERE categoryId = :categoryId")
    fun getAllStreams(categoryId: Int): List<SeriesStream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<SeriesStream>)

    @Update
    suspend fun update(stream: SeriesStream)

    @Query("SELECT * FROM series_stream WHERE lastPlayed > 0")
    fun getRecentStreams(): List<SeriesStream>

    @Query("DELETE FROM series_stream")
    suspend fun deleteAllStreams()
}
