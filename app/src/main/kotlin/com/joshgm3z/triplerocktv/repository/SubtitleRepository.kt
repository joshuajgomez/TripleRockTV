package com.joshgm3z.triplerocktv.repository

data class SubtitleData(
    val title: String,
    val url: String,
)

interface SubtitleRepository {
    suspend fun findSubtitles(query: String): List<SubtitleData>
}