package com.joshgm3z.triplerocktv.repository.data

import com.google.gson.annotations.SerializedName

data class SampleRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("userId") val userId: Int
)

data class SampleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("userId") val userId: Int
)
