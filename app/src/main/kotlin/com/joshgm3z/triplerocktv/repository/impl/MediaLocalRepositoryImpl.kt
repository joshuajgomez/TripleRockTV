package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategoryDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStreamsDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodStreamsDao
import com.joshgm3z.triplerocktv.ui.browse.category.BrowseType
import javax.inject.Inject

class MediaLocalRepositoryImpl @Inject constructor(
    private val vodCategoryDao: VodCategoryDao,
    private val seriesCategoryDao: SeriesCategoryDao,
    private val liveTvCategoryDao: LiveTvCategoryDao,
    private val epgListingDao: EpgListingDao,
    private val vodStreamsDao: VodStreamsDao,
    private val seriesStreamsDao: SeriesStreamsDao,
    private val liveTvStreamsDao: LiveTvStreamsDao,
) : MediaLocalRepository {

    companion object {
        private const val TAG: String = "MediaLocalRepositoryImpl"
    }

    override suspend fun fetchCategories(
        browseType: BrowseType
    ): List<Any> = when (browseType) {
        BrowseType.VideoOnDemand -> vodCategoryDao.getAllCategories()
        BrowseType.Series -> seriesCategoryDao.getAllCategories()
        BrowseType.LiveTV -> liveTvCategoryDao.getAllCategories()
        else -> emptyList()
    }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        epgListingDao.getAllEpgListings()

    override suspend fun searchStreamByName(
        name: String,
        browseType: BrowseType
    ): List<Any> = when (browseType) {
        BrowseType.VideoOnDemand -> vodStreamsDao.searchStreams(name)
        BrowseType.Series -> seriesStreamsDao.searchStreams(name)
        BrowseType.LiveTV -> liveTvStreamsDao.searchStreams(name)
        else -> emptyList()
    }

    override suspend fun fetchStreamsOfCategory(
        categoryId: Int,
        browseType: BrowseType
    ): List<Any> = when (browseType) {
        BrowseType.VideoOnDemand -> vodStreamsDao.getAllStreams(categoryId)
        BrowseType.Series -> seriesStreamsDao.getAllStreams(categoryId)
        BrowseType.LiveTV -> liveTvStreamsDao.getAllStreams(categoryId)
        else -> emptyList()
    }

    override suspend fun fetchStream(
        streamId: Int,
        browseType: BrowseType,
    ): Any? = when (browseType) {
        BrowseType.VideoOnDemand -> vodStreamsDao.getStream(streamId)
        BrowseType.LiveTV -> liveTvStreamsDao.getStream(streamId)
        BrowseType.Series -> seriesStreamsDao.getStream(streamId)
        else -> null
    }

}