package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.MIN_DURATION_LEFT
import com.joshgm3z.triplerocktv.core.repository.room.MIN_PLAYBACK_DURATION
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.collections.filter

class MediaLocalRepositoryImpl @Inject constructor(
    private val epgListingDao: EpgListingDao,
    private val seriesStreamsDao: SeriesStreamsDao,
    private val streamDataDao: StreamDataDao,
    private val categoryDataDao: CategoryDataDao,
) : MediaLocalRepository {

    override suspend fun fetchCategories(
        streamType: StreamType
    ): List<CategoryData> = categoryDataDao.getAllOfType(streamType)

    override suspend fun fetchCategoriesByTitleKey(
        streamType: StreamType,
        titleKey: String
    ): List<CategoryData> {
        return categoryDataDao.getAllOfTypeWithTitleKey(streamType, titleKey)
    }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        epgListingDao.getAllEpgListings()

    override suspend fun fetchStreamsOfCategory(
        categoryId: Int,
        streamType: StreamType
    ): List<Any> = when (streamType) {
        StreamType.Series -> seriesStreamsDao.getAllOfCategory(categoryId)
        else -> streamDataDao.getAllFromCategoryAndType(categoryId, streamType)
    }.apply {
        Logger.info("fetchStreamsOfCategory($categoryId, $streamType): $this")
    }

    override suspend fun fetchStream(
        streamId: Int,
        streamType: StreamType,
    ): StreamData = streamDataDao.getByStreamId(streamId)

    override suspend fun fetchEpisode(
        episodeId: Int,
        seriesId: Int,
    ): Episode {
        val seriesStream = seriesStreamsDao.getBySeriesId(seriesId)
        seriesStream.seasons?.forEach { season ->
            season.episodes.forEach { episode ->
                if (episode.id == episodeId) return episode
            }
        }
        throw Exception("Episode $episodeId for series $seriesId not found")
    }

    override fun streamDataFlow(
        streamId: Int,
        streamType: StreamType,
    ): Flow<StreamData> = when (streamType) {
        StreamType.VideoOnDemand -> streamDataDao.streamDataFlow(streamId)
        else -> streamDataDao.streamDataFlow(streamId)
    }

    override fun seriesStreamFlow(streamId: Int): Flow<SeriesStream> =
        seriesStreamsDao.seriesStreamFlow(streamId)

    override suspend fun isContentEmpty(): Boolean = categoryDataDao.getAll().isEmpty()
            && epgListingDao.getAllEpgListings().isEmpty()

    override suspend fun fetchRecentlyPlayedStreamData(streamType: StreamType): List<StreamData> {
        return streamDataDao.getLastPlayed10(streamType)
    }

    override suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream> {
        val recentSeries = seriesStreamsDao.getLastPlayed10().filter { it.timeLeftInLastEpisode() }
        return recentSeries.sortedByDescending { it.lastPlayed }
    }

    private fun SeriesStream.timeLeftInLastEpisode(): Boolean {
        seasons?.forEach { season ->
            season.episodes.forEach { episode ->
                if (episode.id == lastPlayedEpisodeId
                    && episode.timeRemaining() > MIN_DURATION_LEFT
                    && episode.playedDuration > MIN_PLAYBACK_DURATION
                ) {
                    return true
                }
            }
        }
        return false
    }

    override suspend fun fetchMyList(streamType: StreamType): List<StreamData> {
        return streamDataDao.getMyList5(streamType)
    }

    override suspend fun fetchNewlyAdded(streamType: StreamType): List<StreamData> {
        return if (streamType == StreamType.VideoOnDemand) streamDataDao.getNewlyAdded10()
        else emptyList()
    }

    override suspend fun fetchMyListSeries(): List<SeriesStream> {
        return seriesStreamsDao.getMyList10()
    }

    override suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType
    ) = when (streamType) {
        StreamType.VideoOnDemand -> streamDataDao.updatePlayedDuration(streamId, positionMs)
        else -> {}
    }

    override suspend fun updateEpisodePlayedDuration(
        seriesId: Int,
        episodeId: Int,
        positionMs: Long,
    ) = updateEpisode(seriesId, episodeId) { it.copy(playedDuration = positionMs) }

    override suspend fun updateLastPlayedTimestamp(
        streamId: Int,
        streamType: StreamType,
        timeStamp: Long
    ) {
        streamDataDao.updateLastPlayedTimestamp(streamId, timeStamp)
    }

    override suspend fun updateEpisodeLastPlayedTimestamp(
        episodeId: Int,
        seriesId: Int,
        timeStamp: Long
    ) = updateEpisode(
        episodeId = episodeId, seriesId = seriesId,
        doUpdate = { it.copy(lastPlayed = timeStamp) },
        doUpdateSeries = {
            it.copy(
                lastPlayed = timeStamp,
                lastPlayedEpisodeId = episodeId
            )
        },
    )

    private suspend fun updateEpisode(
        episodeId: Int,
        seriesId: Int,
        doUpdateSeries: (SeriesStream) -> SeriesStream = { it },
        doUpdate: (Episode) -> Episode,
    ) {
        Logger.debug("episodeId = [${episodeId}], seriesId = [${seriesId}]")
        val seriesStream = seriesStreamsDao.getBySeriesId(seriesId)
        val updatedSeasons = seriesStream.seasons?.map { season ->
            val updatedEpisodes = season.episodes.map { episode ->
                if (episode.id == episodeId) doUpdate(episode)
                else episode
            }
            season.copy(episodes = updatedEpisodes)
        }
        val updatedSeriesStream = seriesStream.copy(seasons = updatedSeasons)
        seriesStreamsDao.update(doUpdateSeries(updatedSeriesStream))
    }

    override suspend fun updateMyList(streamId: Int, add: Boolean) {
        streamDataDao.updateMyList(streamId, add)
    }

    override suspend fun updateMyListSeries(seriesId: Int, add: Boolean) {
        seriesStreamsDao.updateMyList(seriesId, add)
    }

    override suspend fun updateSelectedSubtitle(
        streamId: Int,
        language: String,
        title: String,
        url: String?
    ) {
        streamDataDao.updateSubtitleLanguage(streamId, language, title)
        url?.let {
            streamDataDao.updateSubtitleUrl(streamId, it)
        }
    }
}
