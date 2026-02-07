package com.joshgm3z.triplerocktv.repository.data

import com.google.gson.annotations.SerializedName
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing

data class IptvEpgResponse(
    @SerializedName("epg_listings") val epgListings: List<IptvEpgListing>
)