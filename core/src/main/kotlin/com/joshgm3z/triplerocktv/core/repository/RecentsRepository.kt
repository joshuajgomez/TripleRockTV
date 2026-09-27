package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import kotlinx.coroutines.flow.Flow

interface RecentsRepository {

    fun recentlyPlayedStreamDataFlow(streamType: StreamType): Flow<List<StreamData>>

    fun recentlyPlayedSeriesFlow(): Flow<List<SeriesStream>>

    suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long = 0,
        streamType: StreamType,
        seriesId: Int? = null,
        timeStamp: Long = System.currentTimeMillis()
    )

    suspend fun removeRecentlyPlayed(streamId: Int, streamType: StreamType)
}