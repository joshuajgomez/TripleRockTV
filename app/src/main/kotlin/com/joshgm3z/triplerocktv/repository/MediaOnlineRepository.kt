package com.joshgm3z.triplerocktv.repository

enum class MediaLoadingType(val label: String) {
    VideoOnDemand("Video On Demand"),
    Series("Series"),
    LiveTv("Live TV"),
    ParsingPlaylist("Parsing Playlist"),
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
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit,
    )
}
