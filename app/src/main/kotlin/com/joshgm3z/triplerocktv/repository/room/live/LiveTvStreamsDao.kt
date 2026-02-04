package com.joshgm3z.triplerocktv.repository.room.live

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LiveTvStreamsDao {

    @Query("SELECT * FROM live_tv_stream WHERE name LIKE :streamName LIMIT 30")
    fun searchStreams(streamName: String): List<LiveTvStream>

    @Query("SELECT * FROM live_tv_stream WHERE streamId = :streamId")
    fun getStream(streamId: Int): Flow<LiveTvStream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<LiveTvStream>)

    @Query("DELETE FROM live_tv_stream")
    suspend fun deleteAllStreams()
}
