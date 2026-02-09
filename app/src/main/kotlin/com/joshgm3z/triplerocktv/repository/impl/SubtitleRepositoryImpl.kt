package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.repository.retrofit.OpenSubtitlesService
import com.joshgm3z.triplerocktv.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class SubtitleRepositoryImpl
@Inject
constructor(
    scope: CoroutineScope,
) : SubtitleRepository {

    private val openSubtitlesService: OpenSubtitlesService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.opensubtitles.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenSubtitlesService::class.java)
    }

    private var isAuthenticated = false

    init {
        scope.launch {
            try {
                val result = openSubtitlesService.login()
                Logger.debug("result = [$result]")
                isAuthenticated = result.status == 200
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun findSubtitles(query: String): List<SubtitleData> {
        Logger.debug("query = [${query}]")
        try {
            val response = openSubtitlesService.searchSubtitles(query = query)
            Logger.debug("response = [$response]")
            return response.data.map {
                SubtitleData(
                    title = it.attributes.featureDetails?.title ?: "",
                    url = it.attributes.url ?: ""
                )
            }
        } catch (e: HttpException) {
            e.printStackTrace()
        }
        return emptyList()
    }

    override suspend fun storeSubtitle(subtitleData: SubtitleData) {
        TODO("Not yet implemented")
    }
}