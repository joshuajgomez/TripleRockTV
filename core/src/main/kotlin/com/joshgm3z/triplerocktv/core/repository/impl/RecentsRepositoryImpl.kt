package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.RecentsRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayed
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayedDao
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import javax.inject.Inject

class RecentsRepositoryImpl
@Inject constructor(
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val streamDataDao: StreamDataDao,
    private val seriesStreamsDao: SeriesStreamsDao,
    private val onlineRepository: MediaOnlineRepository,
) : RecentsRepository {
    override suspend fun fetchRecentlyPlayedStreamData(streamType: StreamType): List<StreamData> {
        return recentlyPlayedDao.getRecentlyPlayedOfType(streamType).map {
            streamDataDao.getByStreamId(it.id)
        }
    }

    override suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream> {
        return recentlyPlayedDao
            .getRecentlyPlayedByType(StreamType.Series)
            .mapNotNull { recentlyPlayed ->
                val seriesId = recentlyPlayed.seriesId ?: return@mapNotNull null
                val series = with(seriesStreamsDao.getBySeriesId(seriesId)) {
                    when {
                        !seasons.isNullOrEmpty() -> this
                        else -> {
                            onlineRepository.getSeriesDataAndUpdate(seriesId)
                            seriesStreamsDao.getBySeriesId(seriesId)
                        }
                    }
                }
                series.lastPlayedEpisodeId = recentlyPlayed.id
                if (series.hasStartedLastEpisode(recentlyPlayed)) series else null
            }
    }

    private fun SeriesStream.hasStartedLastEpisode(recent: RecentlyPlayed): Boolean {
        seasons?.forEach { season ->
            season.episodes.forEach { episode ->
                if (episode.id == lastPlayedEpisodeId) {
                    episode.recentlyPlayed = recent
                    return episode.startedWatching // Uses the logic defined in Episode class
                }
            }
        }
        return false
    }

    override suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType,
        seriesId: Int?,
        timeStamp: Long
    ) = recentlyPlayedDao.insert(
        RecentlyPlayed(
            id = streamId,
            seriesId = seriesId,
            playedDuration = positionMs,
            streamType = streamType,
            added = timeStamp,
        )
    )
}
