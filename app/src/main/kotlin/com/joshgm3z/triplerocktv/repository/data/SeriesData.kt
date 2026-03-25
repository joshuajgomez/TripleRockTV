package com.joshgm3z.triplerocktv.repository.data

import com.google.gson.annotations.SerializedName
import com.joshgm3z.triplerocktv.repository.room.MIN_DURATION_LEFT
import com.joshgm3z.triplerocktv.repository.room.MIN_PLAYBACK_DURATION
import com.joshgm3z.triplerocktv.repository.room.toTextTime
import com.joshgm3z.triplerocktv.ui.login.UserInfo

data class IptvSeries(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("series_id") val seriesId: Int,
    @SerializedName("cover") val cover: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("cast") val cast: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("last_modified") val lastModified: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("backdrop_path") val backdropPath: List<String>,
    @SerializedName("episode_run_time") val episodeRunTime: String,
    @SerializedName("youtube_trailer") val youtubeTrailer: String,
)

data class SeriesDetailResponse(
    val info: SeriesInfo,
    val seasons: List<SeasonData>,
    val episodes: Map<Int, List<Episode>> // Key is the season number (e.g., "1")
)

data class SeasonData(
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    val id: Int?,
    val name: String?, // e.g., "Season 1"
    val overview: String?,
    @SerializedName("season_number") val seasonNumber: Int?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
)

data class SeriesInfo(
    val name: String?,
    val cover: String?,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val releaseDate: String?,
    @SerializedName("last_modified") val lastModified: String?,
    val rating: String?,
    @SerializedName("rating_5_points") val rating5Points: Double?,
    val backdrop_path: List<String>?,
    val youtube_trailer: String?,
    val category_id: String?,
)

data class Episode(
    val id: Int,
    val episode_num: Int,
    val title: String,
    val container_extension: String, // e.g., "mp4" or "mkv"
    val season: Int,
    val added: String,
    @SerializedName("info")
    val episodeInfo: EpisodeInfo?,
    val lastPlayed: Long = 0,
    val playedDuration: Long = 0,
) {
    fun videoUrl(userInfo: UserInfo) =
        "${userInfo.webUrl}/series/${userInfo.username}/${userInfo.password}/$id.$container_extension"

    fun totalDurationMs(): Long = episodeInfo?.duration_secs?.times(1000L) ?: 0L

    fun progressPercent(): Int = when {
        totalDurationMs() == 0L -> 0
        else -> ((playedDuration.toDouble() / totalDurationMs()) * 100).toInt()
    }

    fun timeRemaining(): Long = when {
        totalDurationMs() == 0L -> 0L
        else -> (totalDurationMs() - playedDuration)
    }

    fun timeRemainingText(): String = when {
        totalDurationMs() == 0L -> ""
        else -> timeRemaining().toTextTime().let { "$it remaining" }
    }

    val startedWatching: Boolean
        get() = playedDuration > MIN_PLAYBACK_DURATION
                && totalDurationMs() != 0L
                && timeRemaining() > MIN_DURATION_LEFT
}

data class EpisodeInfo(
    val releasedate: String?,
    val plot: String?,
    val duration_secs: Int?,
    val duration: String?,
    val movie_image: String?,
    val rating: String?,
    val season: String?,
)