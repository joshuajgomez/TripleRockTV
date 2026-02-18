package com.joshgm3z.triplerocktv.repository

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
}
