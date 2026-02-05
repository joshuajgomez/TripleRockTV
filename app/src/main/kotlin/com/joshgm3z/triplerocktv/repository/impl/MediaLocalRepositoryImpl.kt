package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategoryDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStreamsDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
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
    override suspend fun fetchAllCategories(): Map<BrowseType, List<VodCategory>> {
        return mapOf(
            BrowseType.VideoOnDemand to emptyList(),
            BrowseType.Series to emptyList(),
            BrowseType.EPG to emptyList(),
            BrowseType.LiveTV to emptyList(),
        )
    }

    override suspend fun fetchVodCategories(): List<VodCategory> =
        vodCategoryDao.getAllCategories()

    override suspend fun fetchSeriesCategories(): List<SeriesCategory> =
        seriesCategoryDao.getAllCategories()

    override suspend fun fetchLiveTvCategories(): List<LiveTvCategory> =
        liveTvCategoryDao.getAllCategories()

    override suspend fun fetchIptvEpgCategories(): List<IptvEpgListing> =
        epgListingDao.getAllEpgListings()

    companion object {
        private const val TAG: String = "MediaLocalRepositoryImpl"
    }

    override suspend fun fetchCategories(
        browseType: BrowseType,
        onSuccess: (List<VodCategory>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.i(TAG, "fetchCategories: entry")
        onSuccess(vodCategoryDao.getAllCategories())
    }

    override suspend fun searchStreamByName(
        name: String,
        onSearchResult: (List<VodStream>) -> Unit
    ) {
        onSearchResult(vodStreamsDao.searchStreams(name))
    }

    override suspend fun fetchStreams(categoryId: Int, browseType: BrowseType): List<Any> =
        when (browseType) {
            BrowseType.VideoOnDemand -> vodStreamsDao.getAllStreams(categoryId)
            BrowseType.Series -> seriesStreamsDao.getAllStreams(categoryId)
            BrowseType.LiveTV -> liveTvStreamsDao.getAllStreams(categoryId)
            else -> emptyList()
        }

    override suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (VodStream) -> Unit,
        onError: (String) -> Unit
    ) {
        vodStreamsDao.getStream(streamId).collect { stream ->
            Log.i(TAG, "fetchMediaDataById: $stream")
            onSuccess(stream)
        }
    }

}