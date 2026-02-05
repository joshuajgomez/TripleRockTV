package com.joshgm3z.triplerocktv.repository.room.vod

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import kotlinx.coroutines.flow.Flow

@Dao
interface VodStreamsDao {
    @Query("SELECT * FROM vod_stream WHERE categoryId = :categoryId")
    fun getAllStreams(categoryId: Int): List<VodStream>

    @Query("SELECT * FROM vod_stream WHERE name LIKE :streamName")
    fun searchStreams(streamName: String): List<VodStream>

    @Query("SELECT * FROM vod_stream WHERE streamId = :streamId")
    fun getStream(streamId: Int): VodStream

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<VodStream>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(streams: List<SeriesStream>)

    @Query("DELETE FROM vod_stream")
    suspend fun deleteAllStreams()
}
