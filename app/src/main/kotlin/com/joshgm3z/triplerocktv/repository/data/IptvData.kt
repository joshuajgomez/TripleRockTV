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
    @SerializedName("added") val added: String
)
