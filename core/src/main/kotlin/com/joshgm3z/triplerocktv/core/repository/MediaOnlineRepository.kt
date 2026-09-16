package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata

enum class StreamType {
    VideoOnDemand,
    LiveTV,
    Series,
}

interface MediaOnlineRepository {

    suspend fun updateAllCategories()

    suspend fun getMovieDataAndUpdate(streamId: Int): MovieMetadata?

    suspend fun getMovieMetadata(streamId: Int): MovieMetadata?

    suspend fun getSeriesDataAndUpdate(streamId: Int)

    suspend fun getShortEpgListing(streamId: Int): List<IptvEpgListing>

    suspend fun fetchStreams(streamType: StreamType, categoryId: Int)
}
