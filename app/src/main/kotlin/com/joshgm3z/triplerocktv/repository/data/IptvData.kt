package com.joshgm3z.triplerocktv.repository.data

import com.google.gson.annotations.SerializedName
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing

data class IptvCategory(
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int
)

data class IptvStream(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("rating") val rating: String?,
    @SerializedName("container_extension") val containerExtension: String?,
    @SerializedName("added") val added: String,
    @SerializedName("epg_channel_id") val epgChannelId: String
)

data class VodInfoResponse(
    @SerializedName("info")
    val info: VodInfo,
    @SerializedName("movie_data")
    val movieData: MovieData
)

data class VodInfo(
    @SerializedName("kinopoisk_url")
    val kinopoiskUrl: String?,
    @SerializedName("tmdb_id")
    val tmdbId: String?,
    val name: String?,
    @SerializedName("o_name")
    val oName: String?,
    @SerializedName("cover_big")
    val coverBig: String?,
    @SerializedName("movie_image")
    val movieImage: String?,
    @SerializedName("releasedate")
    val releaseDate: String?,
    @SerializedName("episode_run_time")
    val episodeRunTime: String?,
    @SerializedName("youtube_trailer")
    val youtubeTrailer: String?,
    val director: String?,
    val actors: String?,
    val cast: String?,
    val description: String?,
    val plot: String?,
    val age: String?,
    @SerializedName("mpaa_rating")
    val mpaaRating: String?,
    val country: String?,
    val genre: String?,
    @SerializedName("backdrop_path")
    val backdropPath: List<String>?,
    val duration: String?,
    @SerializedName("duration_secs")
    val durationSecs: Int?,
    val bitrate: Int?,
    val rating: String?,
)

data class MovieData(
    @SerializedName("stream_id")
    val streamId: Int?,
    val name: String?,
    val added: String?,
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("container_extension")
    val containerExtension: String?
)

data class VideoInfo(
    val index: Int?,
    @SerializedName("codec_name")
    val codecName: String?,
    val width: Int?,
    val height: Int?,
    @SerializedName("display_aspect_ratio")
    val displayAspectRatio: String?,
    @SerializedName("r_frame_rate")
    val frameRate: String?,
    val duration: String?,
    @SerializedName("bit_rate")
    val bitRate: String?
)

data class AudioInfo(
    val index: Int?,
    @SerializedName("codec_name")
    val codecName: String?,
    @SerializedName("sample_rate")
    val sampleRate: String?,
    val channels: Int?,
    @SerializedName("channel_layout")
    val channelLayout: String?,
    @SerializedName("bit_rate")
    val bitRate: String?
)


