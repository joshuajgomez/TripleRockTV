package com.joshgm3z.triplerocktv.repository.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

// SubDL API interface for searching subtitles
interface SubdlService {
    @GET("search")
    suspend fun searchSubtitles(
        @Query("q") query: String
    ): SubdlSearchResponse
}

// Data class for SubDL search response (to be updated with actual fields)
data class SubdlSearchResponse(
    val results: List<SubdlSubtitleItem>
)

data class SubdlSubtitleItem(
    val title: String,
    val url: String
)

