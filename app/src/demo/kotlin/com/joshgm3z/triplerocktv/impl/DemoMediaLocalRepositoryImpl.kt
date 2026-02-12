package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.DemoData
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.BrowseType
import javax.inject.Inject

class DemoMediaLocalRepositoryImpl
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
        DemoData.Companion.getSampleIptvEpgListings()

    override suspend fun searchStreamByName(
        name: String,
        browseType: BrowseType
    ): List<Any> = when (browseType) {
        BrowseType.VideoOnDemand -> DemoData.Companion.sampleVodStreams.filter {
            it.name.contains(name, ignoreCase = true)
        }

        BrowseType.Series -> DemoData.Companion.getSampleSeriesStreams().filter {
            it.name.contains(name, ignoreCase = true)
        }

        BrowseType.LiveTV -> DemoData.Companion.sampleLiveStreams.filter {
            it.name.contains(name, ignoreCase = true)
        }

        else -> emptyList()
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

    override suspend fun fetchStream(
        streamId: Int,
        browseType: BrowseType
    ): Any? = DemoData.allStreams.firstOrNull {
        when (it) {
            is VodStream -> it.streamId == streamId
            is SeriesStream -> it.seriesId == streamId
            is LiveTvStream -> it.streamId == streamId
            else -> false
        }
    }

    override suspend fun isContentEmpty(): Boolean = false

    override suspend fun fetchRecentlyPlayed(): List<Any> {
        return DemoData.sampleVodStreams.subList(0, 2) +
                DemoData.sampleLiveStreams.subList(0, 1)

    }
}