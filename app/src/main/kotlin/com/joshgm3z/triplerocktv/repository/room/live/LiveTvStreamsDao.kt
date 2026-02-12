package com.joshgm3z.triplerocktv.repository.room.live

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream

@Dao
interface LiveTvStreamsDao {

    @Query("SELECT * FROM live_tv_stream WHERE name LIKE '%' || :streamName || '%'")
    fun searchStreams(streamName: String): List<LiveTvStream>

    @Query("SELECT * FROM live_tv_stream WHERE streamId = :streamId")
    fun getStream(streamId: Int): LiveTvStream

    @Query("SELECT * FROM live_tv_stream WHERE categoryId = :categoryId")
    fun getAllStreams(categoryId: Int): List<LiveTvStream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<LiveTvStream>)

    @Update
    suspend fun update(stream: LiveTvStream)

    @Query("SELECT * FROM live_tv_stream WHERE lastPlayed > 0")
    fun getRecentStreams(): List<LiveTvStream>

    @Query("DELETE FROM live_tv_stream")
    suspend fun deleteAllStreams()
}
