package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.impl.helper.EPGFetcher
import com.joshgm3z.triplerocktv.repository.impl.helper.LiveTvFetcher
import com.joshgm3z.triplerocktv.repository.impl.helper.SeriesFetcher
import com.joshgm3z.triplerocktv.repository.impl.helper.VodFetcher
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.util.Logger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaOnlineRepositoryImpl
@Inject constructor(
    private val localDatastore: LocalDatastore,
    private val seriesFetcher: SeriesFetcher,
    private val vodFetcher: VodFetcher,
    private val liveTvFetcher: LiveTvFetcher,
    private val epgFetcher: EPGFetcher,
) : MediaOnlineRepository {
    companion object {
        const val LIMIT = 5
        lateinit var username: String
        lateinit var password: String
    }

    init {
        localDatastore.getLoginCredentials {
            username = it.username
            password = it.password
            val iptvService = getIptvService(it.webUrl)
            seriesFetcher.iptvService = iptvService
            vodFetcher.iptvService = iptvService
            liveTvFetcher.iptvService = iptvService
            epgFetcher.iptvService = iptvService
        }
    }

    fun getIptvService(serverUrl: String): IptvService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(IptvService::class.java)
    }

    override suspend fun fetchContent(
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        try {
            vodFetcher.fetchContent(onFetch, onError)
            seriesFetcher.fetchContent(onFetch, onError)
            liveTvFetcher.fetchContent(onFetch, onError)
            epgFetcher.fetchContent(onFetch, onError)
        } catch (e: Exception) {
            Logger.error(e.message ?: "Error fetching content")
            onError("Unable to fetch categories.", e.message ?: "")
        } finally {
            localDatastore.setLastContentUpdate(System.currentTimeMillis())
        }
    }
}
