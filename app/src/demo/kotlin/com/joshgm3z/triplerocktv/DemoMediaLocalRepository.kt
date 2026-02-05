package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.category.BrowseType
import javax.inject.Inject

class DemoMediaLocalRepository
@Inject
constructor() : MediaLocalRepository {
    override suspend fun fetchCategories(
        browseType: BrowseType
    ): List<Any> = when (browseType) {
        BrowseType.VideoOnDemand -> DemoData.sampleVodCategory()
        BrowseType.Series -> DemoData.getSampleSeriesCategories()
        BrowseType.LiveTV -> DemoData.getSampleLiveTvCategories()
        BrowseType.EPG -> DemoData.getSampleIptvEpgListings()
        else -> emptyList()
    }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        DemoData.getSampleIptvEpgListings()

    override suspend fun searchStreamByName(name: String): List<Any> =
        DemoData.allStreams.filter {
            when (it) {
                is VodStream -> it.name.contains(name, ignoreCase = true)
                is SeriesStream -> it.name.contains(name, ignoreCase = true)
                is LiveTvStream -> it.name.contains(name, ignoreCase = true)
                else -> false
            }
        }

    override suspend fun fetchStreamsOfCategory(
        categoryId: Int,
        browseType: BrowseType
    ): List<Any> = when (browseType) {
        BrowseType.VideoOnDemand -> DemoData.sampleVodStreams.filter { it.categoryId == categoryId }
        BrowseType.Series -> DemoData.getSampleSeriesStreams()
            .filter { it.categoryId == categoryId }

        BrowseType.LiveTV -> DemoData.sampleLiveStreams.filter { it.categoryId == categoryId }
        else -> emptyList()
    }

    override suspend fun fetchStream(streamId: Int): Any = DemoData.allStreams.first {
        when (it) {
            is VodStream -> it.streamId == streamId
            is SeriesStream -> it.seriesId == streamId
            is LiveTvStream -> it.streamId == streamId
            else -> false
        }
    }


}