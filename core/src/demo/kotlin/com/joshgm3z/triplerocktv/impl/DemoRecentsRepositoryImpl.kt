package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.repository.RecentsRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import javax.inject.Inject

class DemoRecentsRepositoryImpl
@Inject constructor() : RecentsRepository {
    override suspend fun fetchRecentlyPlayedStreamData(streamType: StreamType): List<StreamData> {
        return emptyList()
    }

    override suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream> {
        return emptyList()
    }

    override suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType,
        seriesId: Int?,
        timeStamp: Long
    ) {
    }
}