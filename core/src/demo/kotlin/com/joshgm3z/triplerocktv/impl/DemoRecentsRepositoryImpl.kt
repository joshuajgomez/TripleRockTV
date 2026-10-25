package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.repository.RecentsRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class DemoRecentsRepositoryImpl
@Inject constructor() : RecentsRepository {

    override fun recentlyPlayedStreamDataFlow(streamType: StreamType): Flow<List<StreamData>> {
        return emptyFlow()
    }

    override fun recentlyPlayedSeriesFlow(): Flow<List<SeriesStream>> {
        return emptyFlow()
    }

    override suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType,
        seriesId: Int?,
        timeStamp: Long
    ) {
    }

    override suspend fun removeRecentlyPlayed(
        streamId: Int,
        streamType: StreamType
    ) {
    }

    override suspend fun updateSelectedSubtitle(
        streamId: Int,
        language: String,
        title: String,
        url: String?
    ) {
    }
}