package com.joshgm3z.triplerocktv.repository.room.series

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joshgm3z.triplerocktv.repository.room.StreamData
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesStreamsDao {

    @Query("SELECT * FROM series_stream WHERE name LIKE '%' || :streamName || '%'")
    fun searchStreams(streamName: String): List<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE seriesId = :seriesId")
    fun getBySeriesId(seriesId: Int): SeriesStream

    @Query("SELECT * FROM series_stream WHERE seriesId = :seriesId")
    fun seriesStreamFlow(seriesId: Int): Flow<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE categoryId = :categoryId")
    fun getAllOfCategory(categoryId: Int): List<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE inMyList IS true ORDER BY timeAddedToList DESC LIMIT 10")
    fun getMyList10(): List<SeriesStream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<SeriesStream>)

    @Update
    suspend fun update(stream: SeriesStream)

    @Query("SELECT * FROM series_stream ORDER BY lastPlayed DESC LIMIT 10")
    fun getLastPlayed10(): List<SeriesStream>

    @Query("DELETE FROM series_stream")
    suspend fun deleteAllStreams()

    @Query("UPDATE series_stream SET inMyList = :add, timeAddedToList = :timeAddedToList WHERE seriesId = :seriesId")
    suspend fun updateMyList(
        seriesId: Int,
        add: Boolean,
        timeAddedToList: Long = System.currentTimeMillis()
    )
    fun updateMyList(seriesId: Int, add: Boolean) {}
}
