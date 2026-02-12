package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.ui.browse.BrowseType

interface MediaLocalRepository {

    suspend fun fetchCategories(browseType: BrowseType): List<Any>

    suspend fun fetchEpgListings(): List<IptvEpgListing>

    suspend fun searchStreamByName(name: String, browseType: BrowseType): List<Any>

    suspend fun fetchStreamsOfCategory(categoryId: Int, browseType: BrowseType): List<Any>

    suspend fun fetchStream(streamId: Int, browseType: BrowseType): Any?

    suspend fun isContentEmpty(): Boolean

    suspend fun fetchRecentlyPlayed(): List<Any>
}
