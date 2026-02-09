package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.repository.retrofit.SubdlService
import com.joshgm3z.triplerocktv.repository.retrofit.SubdlSubtitleItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class SubtitleRepositoryImpl
@Inject
constructor() : SubtitleRepository {
    private val subdlService: SubdlService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.subdl.com/") // Replace with actual SubDL API base URL if different
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubdlService::class.java)
    }

    override suspend fun findSubtitles(query: String): List<SubtitleData> {
        val response = subdlService.searchSubtitles(query)
        return response.results.map { SubtitleData(title = it.title, url = it.url) }
    }

    override suspend fun storeSubtitle(subtitleData: SubtitleData) {
        TODO("Not yet implemented")
    }
}