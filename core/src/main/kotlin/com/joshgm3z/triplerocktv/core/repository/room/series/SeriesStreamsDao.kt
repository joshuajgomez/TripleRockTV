package com.joshgm3z.triplerocktv.core.repository.room.series

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.joshgm3z.triplerocktv.core.repository.SEARCH_LIMIT
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesStreamsDao {

    @Query("SELECT * FROM series_stream WHERE name LIKE '%' || :streamName || '%' LIMIT :limit")
    fun searchStreams(streamName: String, limit: Int = SEARCH_LIMIT): List<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE seriesId = :seriesId")
    fun getBySeriesId(seriesId: Int): SeriesStream

    @Query("SELECT * FROM series_stream")
    fun getAll(): List<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE seriesId = :seriesId")
    fun seriesStreamFlow(seriesId: Int): Flow<SeriesStream>

    @Query("SELECT * FROM series_stream WHERE categoryId = :categoryId")
    fun getAllOfCategory(categoryId: Int): List<SeriesStream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<SeriesStream>)

    @Transaction
    suspend fun replaceData(seriesStreamList: List<SeriesStream>) {
        deleteAllStreams()
        insertStreams(seriesStreamList)
    }

    @Update
    suspend fun update(stream: SeriesStream)

    @Query("DELETE FROM series_stream")
    suspend fun deleteAllStreams()

}