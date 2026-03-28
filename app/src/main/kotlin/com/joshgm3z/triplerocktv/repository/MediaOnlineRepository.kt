package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.MovieMetadata

enum class StreamType {
    VideoOnDemand,
    LiveTV,
    Series,
}

data class LoadingState(
    val percent: Int = 0,
    val status: LoadingStatus = LoadingStatus.Initial,
    val error: String? = null,
)

enum class LoadingStatus {
    Ongoing,
    Complete,
    Initial,
    Error,
}

interface MediaOnlineRepository {
    suspend fun fetchContent(
        onFetch: (StreamType, LoadingState) -> Unit,
        onError: (String, String) -> Unit,
    )

    suspend fun getMovieDataAndUpdate(streamId: Int, streamType: StreamType)

    suspend fun getMovieMetadata(streamId: Int): MovieMetadata

    suspend fun getSeriesDataAndUpdate(streamId: Int)
}
