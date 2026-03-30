package com.joshgm3z.triplerocktv.core.repository.data

import com.google.gson.annotations.SerializedName
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing

data class IptvEpgResponse(
    @SerializedName("epg_listings") val epgListings: List<IptvEpgListing>
)