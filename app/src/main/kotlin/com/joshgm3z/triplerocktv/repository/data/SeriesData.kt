package com.joshgm3z.triplerocktv.repository.data

import com.google.gson.annotations.SerializedName

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
    @SerializedName("category_id") val categoryId: Int
)

data class SeriesDetailResponse(
    val info: SeriesInfo,
    val seasons: List<Season>,
    val episodes: Map<String, List<Episode>> // Key is the season number (e.g., "1")
)

data class Season(
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    val id: Int?,
    val name: String?, // e.g., "Season 1"
    val overview: String?,
    @SerializedName("season_number") val seasonNumber: Int?,
    @SerializedName("cover") val cover: String?
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
    val youtube_trailer: String?
)

data class Episode(
    val id: String,
    val title: String,
    val container_extension: String, // e.g., "mp4" or "mkv"
    val season: Int
)