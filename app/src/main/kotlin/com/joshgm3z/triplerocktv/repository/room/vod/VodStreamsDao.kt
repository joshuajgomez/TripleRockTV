package com.joshgm3z.triplerocktv.repository.room.vod

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream

@Dao
interface VodStreamsDao {
    @Query("SELECT * FROM vod_stream WHERE categoryId = :categoryId")
    fun getAllStreams(categoryId: Int): List<VodStream>

    @Query("SELECT * FROM vod_stream WHERE name LIKE '%' || :streamName || '%'")
    fun searchStreams(streamName: String): List<VodStream>

    @Query("SELECT * FROM vod_stream WHERE streamId = :streamId")
    fun getStream(streamId: Int): VodStream

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<VodStream>)

    @Update
    suspend fun update(stream: VodStream)

    @Query("DELETE FROM vod_stream")
    suspend fun deleteAllStreams()

    @Query("SELECT * FROM vod_stream WHERE lastPlayed > 0")
    fun getRecentStreams(): List<VodStream>
}
