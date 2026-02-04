package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.BrowseType

interface MediaLocalRepository {

    suspend fun fetchAllCategories(): Map<BrowseType, List<VodCategory>>

    suspend fun fetchVodCategories(): List<VodCategory>

    suspend fun fetchSeriesCategories(): List<SeriesCategory>

    suspend fun fetchLiveTvCategories(): List<LiveTvCategory>

    suspend fun fetchIptvEpgCategories(): List<IptvEpgListing>

    suspend fun fetchCategories(
        browseType: BrowseType,
        onSuccess: (List<VodCategory>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun searchStreamByName(
        name: String,
        onSearchResult: (List<VodStream>) -> Unit
    )

    suspend fun fetchStreams(categoryId: Int): List<VodStream>

    suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (VodStream) -> Unit,
        onError: (String) -> Unit,
    )
}
