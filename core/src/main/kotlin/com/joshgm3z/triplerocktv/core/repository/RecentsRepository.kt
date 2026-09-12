package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import kotlinx.coroutines.flow.Flow

interface RecentsRepository {

    suspend fun fetchRecentlyPlayedStreamData(streamType: StreamType): List<StreamData>

    fun recentlyPlayedStreamDataFlow(streamType: StreamType): Flow<List<StreamData>>

    suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream>

    fun recentlyPlayedSeriesFlow(): Flow<List<SeriesStream>>

    suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long = 0,
        streamType: StreamType,
        seriesId: Int? = null,
        timeStamp: Long = System.currentTimeMillis()
    )
}