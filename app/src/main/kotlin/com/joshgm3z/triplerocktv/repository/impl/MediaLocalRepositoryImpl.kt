package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.MovieMetadata
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaLocalRepositoryImpl @Inject constructor(
    private val seriesCategoryDao: SeriesCategoryDao,
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
    ): List<StreamData> = when (streamType) {
        StreamType.Series -> emptyList()
        else -> streamDataDao.getAllFromCategoryAndType(categoryId, streamType)
    }.apply {
        Logger.info("fetchStreamsOfCategory($categoryId, $streamType): $this")
    }

    override suspend fun fetchStream(
        streamId: Int,
        streamType: StreamType,
    ): StreamData? = when (streamType) {
        StreamType.Series -> null
        else -> streamDataDao.getByStreamId(streamId)
    }

    override fun streamDataFlow(
        streamId: Int,
        streamType: StreamType,
    ): Flow<StreamData> = when (streamType) {
        StreamType.VideoOnDemand -> streamDataDao.streamDataFlow(streamId)
        else -> streamDataDao.streamDataFlow(streamId)
    }

    override suspend fun isContentEmpty(): Boolean = categoryDataDao.getAll().isEmpty()
            && seriesCategoryDao.getAllCategories().isEmpty()
            && epgListingDao.getAllEpgListings().isEmpty()

    override suspend fun fetchRecentlyPlayed(): List<StreamData> =
        mutableListOf<StreamData>().apply {
            addAll(streamDataDao.getLastPlayed10())
        }

    override suspend fun fetchMyList(): List<StreamData> {
        return streamDataDao.getMyList10()
    }

    override suspend fun updatePlayedDuration(streamId: Int, positionMs: Long) {
        streamDataDao.updatePlayedDuration(streamId, positionMs)
    }

    override suspend fun updateLastPlayedTimestamp(streamId: Int) {
        streamDataDao.updateLastPlayedTimestamp(streamId)
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