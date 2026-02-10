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
                language = "en"
                ),
            SubtitleData(
                title = "The.Equalizer.2.2018.HYBRID.2160p.BluRay.REMUX.HEVC.DV.TrueHD.Atmos.7.1-Flights",
                url = "https://example.com/spanish.srt",
                language = "fr"
            ),
            SubtitleData(
                title = "The.Equalizer.2.2018.HYBRID.2160p.BluRay.REMUX.HEVC.DV.TrueHD.Atmos.7.1-Flights",
                url = "https://example.com/french.srt",
                language = "ro"
            ),
            SubtitleData(
                title = "Wonder.Woman.1984.2020.720p.WEB.H264-NAISU.ur",
                url = "https://example.com/german.srt",
                language = "fi"
            ),
        )
    }

    override suspend fun getSubtitleUrl(fileId: Int): String? {
        return ""
    }
}