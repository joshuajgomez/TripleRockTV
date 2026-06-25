package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.core.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.core.repository.retrofit.OpenSubtitlesDownloadRequest
import com.joshgm3z.triplerocktv.core.repository.retrofit.OpenSubtitlesService
import com.joshgm3z.triplerocktv.core.util.FirebaseConfig
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

private val defaultUrl = "https://api.opensubtitles.com/api/v1/"

class SubtitleRepositoryImpl
@Inject
constructor(
    scope: CoroutineScope,
    firebaseConfig: FirebaseConfig,
) : SubtitleRepository {

    private val baseUrl = firebaseConfig.getString("open_subtitles_api_url") ?: defaultUrl

    private val openSubtitlesService: OpenSubtitlesService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenSubtitlesService::class.java)
    }

    private var authToken: String? = null

    init {
        scope.launch {
            try {
                val result = openSubtitlesService.login()
                Logger.debug("result = [$result]")
                authToken = result.token
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
            val list = mutableListOf<SubtitleData>()
            response.data.map {
                it.attributes.files?.map { file ->
                    list.add(
                        SubtitleData(
                            title = file.fileName,
                            language = it.attributes.language,
                            fileId = file.fileId,
                            downloadCount = it.attributes.downloadCount,
                        )
                    )
                } ?: emptyList()
            }
            return list
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    override suspend fun getSubtitleUrl(fileId: Int): String? {
        val token = authToken ?: return null
        return try {
            val response = openSubtitlesService.download(
                token = "Bearer $token",
                body = OpenSubtitlesDownloadRequest(fileId)
            )
            response.link
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
