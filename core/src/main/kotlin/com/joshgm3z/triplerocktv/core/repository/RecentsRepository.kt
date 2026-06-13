package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData

interface RecentsRepository {
    suspend fun fetchRecentlyPlayedStreamData(streamType: StreamType): List<StreamData>

    suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream>

    suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType,
        seriesId: Int? = null,
        timeStamp: Long = System.currentTimeMillis()
    )
}