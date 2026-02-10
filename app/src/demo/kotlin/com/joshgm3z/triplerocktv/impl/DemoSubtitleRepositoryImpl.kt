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
                title = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                url = "https://example.com/english.srt",
            ),
            SubtitleData(
                title = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                url = "https://example.com/spanish.srt",
            ),
            SubtitleData(
                title = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                url = "https://example.com/french.srt",
            ),
            SubtitleData(
                title = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                url = "https://example.com/german.srt",
            ),
        )
    }

    override suspend fun storeSubtitle(subtitleData: SubtitleData) {
        delay(1000)
    }
}