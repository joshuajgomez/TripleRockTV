package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing

interface MediaLocalRepository {

    suspend fun fetchCategories(streamType: StreamType): List<CategoryData>

    suspend fun fetchEpgListings(): List<IptvEpgListing>

    suspend fun searchStreamByName(name: String, streamType: StreamType): List<StreamData>

    suspend fun fetchStreamsOfCategory(categoryId: Int, streamType: StreamType): List<StreamData>

    suspend fun fetchStream(streamId: Int, streamType: StreamType): StreamData?

    suspend fun isContentEmpty(): Boolean

    suspend fun fetchRecentlyPlayed(): List<StreamData>

    suspend fun updateLastPlayed(streamData: StreamData, time: Long)
}
