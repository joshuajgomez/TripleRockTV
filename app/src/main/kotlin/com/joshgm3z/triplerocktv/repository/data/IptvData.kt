package com.joshgm3z.triplerocktv.repository.data

import com.google.gson.annotations.SerializedName

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
    @SerializedName("added") val added: String
)

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
    @SerializedName("category_id") val categoryId: String
)

data class IptvEpgResponse(
    @SerializedName("epg_listings") val epgListings: List<IptvEpgListing>
)

data class IptvEpgListing(
    @SerializedName("id") val id: String,
    @SerializedName("epg_id") val epgId: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("lang") val lang: String?,
    @SerializedName("start") val start: String?,
    @SerializedName("end") val end: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("channel_id") val channelId: String?,
    @SerializedName("start_timestamp") val startTimestamp: String?,
    @SerializedName("stop_timestamp") val stopTimestamp: String?
)
