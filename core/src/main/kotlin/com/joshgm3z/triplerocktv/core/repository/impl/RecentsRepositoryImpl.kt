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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentsRepositoryImpl
@Inject constructor(
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val streamDataDao: StreamDataDao,
    private val seriesStreamsDao: SeriesStreamsDao,
    private val onlineRepository: MediaOnlineRepository,
) : RecentsRepository {

    override suspend fun fetchRecentlyPlayedStreamData(
        streamType: StreamType
    ): List<StreamData> {
        return recentlyPlayedDao.getRecentlyPlayedOfType(streamType).mapNotNull {
            val streamData = streamDataDao.getByStreamId(it.id) ?: return@mapNotNull null
            when {
                streamData.movieMetadata != null -> streamData
                else -> {
                    onlineRepository.getMovieDataAndUpdate(it.id)
                    streamDataDao.getByStreamId(it.id) ?: return@mapNotNull null
                }
            }.apply {
                recentlyPlayed = it
            }
        }
    }

    override fun recentlyPlayedStreamDataFlow(streamType: StreamType): Flow<List<StreamData>> {
        return recentlyPlayedDao.recentlyPlayedFlowOfType(streamType).map {
            it.mapNotNull { recentlyPlayed ->
                val streamData = withContext(Dispatchers.IO) {
                    streamDataDao.getByStreamId(recentlyPlayed.id)
                } ?: return@mapNotNull null
                when {
                    streamData.movieMetadata != null
                            || streamData.streamType == StreamType.LiveTV -> streamData

                    else -> {
                        onlineRepository.getMovieDataAndUpdate(recentlyPlayed.id)
                        withContext(Dispatchers.IO) {
                            streamDataDao.getByStreamId(recentlyPlayed.id)
                        } ?: return@mapNotNull null
                    }
                }.apply {
                    this.recentlyPlayed = recentlyPlayed
                }
            }
        }
    }

    override suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream> {
        return recentlyPlayedDao
            .getRecentlyPlayedByType(StreamType.Series)
            .mapNotNull { recentlyPlayed ->
                val seriesId = recentlyPlayed.seriesId ?: return@mapNotNull null
                val seriesStream =
                    seriesStreamsDao.getBySeriesId(seriesId) ?: return@mapNotNull null
                val series = with(seriesStream) {
                    when {
                        !seasons.isNullOrEmpty() -> this
                        else -> {
                            onlineRepository.getSeriesDataAndUpdate(seriesId)
                            seriesStreamsDao.getBySeriesId(seriesId) ?: return@mapNotNull null
                        }
                    }
                }
                series.lastPlayedEpisodeId = recentlyPlayed.id
                if (series.hasStartedLastEpisode(recentlyPlayed)) series else null
            }
    }

    override fun recentlyPlayedSeriesFlow(): Flow<List<SeriesStream>> {
        return recentlyPlayedDao.recentlyPlayedFlowOfType(StreamType.Series).map {
            it.mapNotNull { recentlyPlayed ->
                val seriesId = recentlyPlayed.seriesId ?: return@mapNotNull null
                val seriesStream = withContext(Dispatchers.IO) {
                    seriesStreamsDao.getBySeriesId(seriesId)
                } ?: return@mapNotNull null
                val series = with(seriesStream) {
                    when {
                        !seasons.isNullOrEmpty() -> this
                        else -> {
                            onlineRepository.getSeriesDataAndUpdate(seriesId)
                            withContext(Dispatchers.IO) {
                                seriesStreamsDao.getBySeriesId(seriesId)
                            } ?: return@mapNotNull null
                        }
                    }
                }
                series.lastPlayedEpisodeId = recentlyPlayed.id
                if (series.hasStartedLastEpisode(recentlyPlayed)) series else null
            }
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
