package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import javax.inject.Inject

class SubtitleRepositoryImpl
@Inject
constructor() : SubtitleRepository {
    override suspend fun findSubtitles(query: String): List<SubtitleData> {
        TODO("Not yet implemented")
    }

    override suspend fun storeSubtitle(subtitleData: SubtitleData) {
        TODO("Not yet implemented")
    }
}