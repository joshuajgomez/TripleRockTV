package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import kotlinx.coroutines.flow.Flow

interface MediaLocalRepository {

    suspend fun fetchCategories(streamType: StreamType): List<CategoryData>

    suspend fun fetchCategoriesByTitleKey(
        streamType: StreamType,
        titleKey: String
    ): List<CategoryData>

    suspend fun fetchEpgListings(): List<IptvEpgListing>

    suspend fun fetchStreamsOfCategory(categoryId: Int, streamType: StreamType): List<Any>

    suspend fun fetchStream(streamId: Int, streamType: StreamType): StreamData

    suspend fun fetchEpisode(episodeId: Int, seriesId: Int): Episode?

    suspend fun streamDataFlow(streamId: Int, streamType: StreamType): Flow<StreamData>

    fun seriesStreamFlow(seriesId: Int): Flow<SeriesStream>

    suspend fun isContentEmpty(): Boolean

    suspend fun fetchMyList(streamType: StreamType): List<StreamData>

    suspend fun fetchNewlyAdded(streamType: StreamType): List<StreamData>

    suspend fun fetchMyListSeries(): List<SeriesStream>

    suspend fun updateMyList(
        streamId: Int,
        streamType: StreamType,
        add: Boolean
    )

    suspend fun updateSelectedSubtitle(streamId: Int, language: String, title: String, url: String?)

    suspend fun numberOfFiles(type: StreamType): Int
}