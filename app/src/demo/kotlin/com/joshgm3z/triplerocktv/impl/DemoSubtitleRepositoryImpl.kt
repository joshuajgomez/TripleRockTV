package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class DemoSubtitleRepositoryImpl
@Inject
constructor() : SubtitleRepository {
    override suspend fun findSubtitles(query: String): List<SubtitleData> {
        return listOf(
            SubtitleData(
                title = "English",
                url = "https://example.com/english.srt",
            ),
            SubtitleData(
                title = "Spanish",
                url = "https://example.com/spanish.srt",
            ),
            SubtitleData(
                title = "French",
                url = "https://example.com/french.srt",
            ),
            SubtitleData(
                title = "German",
                url = "https://example.com/german.srt",
            ),
        )
    }

    override suspend fun storeSubtitle(subtitleData: SubtitleData) {
        delay(1000)
    }
}