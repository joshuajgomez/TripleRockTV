package com.joshgm3z.triplerocktv.repository

data class SubtitleData(
    val title: String,
    val language: String? = null,
    val fileId: Int,
    val url: String? = null,
)

interface SubtitleRepository {
    suspend fun findSubtitles(query: String): List<SubtitleData>
    suspend fun getSubtitleUrl(fileId: Int): String?
}