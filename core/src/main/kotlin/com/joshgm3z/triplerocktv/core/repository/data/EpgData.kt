package com.joshgm3z.triplerocktv.core.repository.data

import com.google.gson.annotations.SerializedName
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import java.time.ZonedDateTime

data class IptvEpgResponse(
    @SerializedName("epg_listings") val epgListings: List<IptvEpgListing>
)

data class XmlTvProgram(
    var start: ZonedDateTime,
    var stop: ZonedDateTime,
    var id: String,
    var title: String? = null,
    var description: String? = null,
    var icon: String? = null,
    var streamId: Int? = null,
)