package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
    ): Any? = when (streamType) {
        StreamType.Series -> seriesStreamsDao.getBySeriesId(streamId)
        else -> streamDataDao.getByStreamId(streamId)
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

    override suspend fun fetchRecentlyPlayed(): List<StreamData> =
        mutableListOf<StreamData>().apply {
            addAll(streamDataDao.getLastPlayed10())
        }

    override suspend fun fetchMyList(): List<StreamData> {
        return streamDataDao.getMyList10()
    }

    override suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType
    ) = when (streamType) {
        StreamType.VideoOnDemand -> streamDataDao.updatePlayedDuration(streamId, positionMs)
        StreamType.Series -> updateEpisode(streamId) { it.copy(playedDuration = positionMs) }
        else -> {}
    }

    override suspend fun updateLastPlayedTimestamp(
        streamId: Int,
        streamType: StreamType,
        timeStamp: Long
    ) {
        when (streamType) {
            StreamType.Series -> updateEpisode(streamId) { it.copy(lastPlayed = timeStamp) }
            StreamType.VideoOnDemand -> streamDataDao.updateLastPlayedTimestamp(streamId, timeStamp)
            else -> {}
        }
    }

    private suspend fun updateEpisode(streamId: Int, doUpdate: (Episode) -> Episode) {
        val seriesStream = seriesStreamsDao.getBySeriesId(streamId)
        val updatedSeasons = seriesStream.seasons?.map { season ->
            val updatedEpisodes = season.episodes.map { episode ->
                if (episode.id == streamId) doUpdate(episode)
                else episode
            }
            season.copy(episodes = updatedEpisodes)
        }
        val updatedSeriesStream = seriesStream.copy(seasons = updatedSeasons)
        seriesStreamsDao.update(updatedSeriesStream)
    }

    override suspend fun updateMyList(streamId: Int, add: Boolean) {
        streamDataDao.updateMyList(streamId, add)
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