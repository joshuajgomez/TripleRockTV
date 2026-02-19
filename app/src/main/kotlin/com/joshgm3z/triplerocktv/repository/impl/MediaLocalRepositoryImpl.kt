package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
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
    ): List<CategoryData> = when (streamType) {
        StreamType.VideoOnDemand,
        StreamType.LiveTV -> categoryDataDao.getAllOfType(streamType)

        else -> emptyList()
    }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        epgListingDao.getAllEpgListings()

    override suspend fun searchStreamByName(
        name: String,
        streamType: StreamType
    ): List<StreamData> = when (streamType) {
        StreamType.Series -> emptyList()
        else -> streamDataDao.searchByName(name)
    }.apply {
        Logger.info("searchStreamByName($name, $streamType): $this")
    }

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

    override suspend fun isContentEmpty(): Boolean = categoryDataDao.getAll().isEmpty()
            && seriesCategoryDao.getAllCategories().isEmpty()
            && epgListingDao.getAllEpgListings().isEmpty()

    override suspend fun fetchRecentlyPlayed(): List<StreamData> =
        mutableListOf<StreamData>().apply {
            addAll(streamDataDao.getLastPlayed10())
        }

    override suspend fun updateLastPlayed(streamData: StreamData, time: Long) {
        streamDataDao.update(streamData.copy(lastPlayed = time))
    }
}