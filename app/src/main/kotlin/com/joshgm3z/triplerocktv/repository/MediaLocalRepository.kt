package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
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

    fun streamDataFlow(streamId: Int, streamType: StreamType): Flow<StreamData>

    fun seriesStreamFlow(streamId: Int): Flow<SeriesStream>

    suspend fun isContentEmpty(): Boolean

    suspend fun fetchRecentlyPlayed(): List<StreamData>

    suspend fun fetchMyList(): List<StreamData>

    suspend fun updatePlayedDuration(streamId: Int, positionMs: Long, streamType: StreamType)

    suspend fun updateLastPlayedTimestamp(
        streamId: Int,
        streamType: StreamType,
        timeStamp: Long = System.currentTimeMillis()
    )

    suspend fun updateEpisodeLastPlayedTimestamp(
        episodeId: Int,
        seriesId: Int,
        timeStamp: Long = System.currentTimeMillis()
    )

    suspend fun updateEpisodePlayedDuration(episodeId: Int, seriesId: Int, positionMs: Long)

    suspend fun updateMyList(streamId: Int, add: Boolean)

    suspend fun updateSelectedSubtitle(streamId: Int, language: String, title: String, url: String?)
}
