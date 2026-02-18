package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.helper.EPGFetcher
import com.joshgm3z.triplerocktv.repository.impl.helper.SeriesFetcher
import com.joshgm3z.triplerocktv.repository.impl.helper.OnlineDataFetcher
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaOnlineRepositoryImpl
@Inject constructor(
    scope: CoroutineScope,
    private val localDatastore: LocalDatastore,
    private val seriesFetcher: SeriesFetcher,
    private val onlineDataFetcher: OnlineDataFetcher,
    private val epgFetcher: EPGFetcher,
) : MediaOnlineRepository {
    companion object {
        const val LIMIT = 5
        lateinit var username: String
        lateinit var password: String
    }

    init {
        scope.launch {
            fetchIptvService()
        }
    }

    private var iptvService: IptvService? = null

    private suspend fun fetchIptvService() {
        localDatastore.getUserInfo()?.let {
            username = it.username
            password = it.password
            iptvService = getIptvService(it.webUrl)
            seriesFetcher.iptvService = iptvService!!
            onlineDataFetcher.iptvService = iptvService!!
            epgFetcher.iptvService = iptvService!!
        }
        assert(iptvService != null)
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
        onFetch: (StreamType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        if (iptvService == null) {
            fetchIptvService()
        }
        try {
            onlineDataFetcher.fetchContent(
                streamType = StreamType.VideoOnDemand, onError = onError,
                onFetch = { onFetch(StreamType.VideoOnDemand, it) },
            )
            onlineDataFetcher.fetchContent(
                streamType = StreamType.LiveTV, onError = onError,
                onFetch = { onFetch(StreamType.LiveTV, it) },
            )
            seriesFetcher.fetchContent(onFetch, onError)
//            epgFetcher.fetchContent(onFetch, onError)
        } catch (e: Exception) {
            Logger.error(e.message ?: "Error fetching content")
            onError("Unable to fetch categories.", e.message ?: "")
        } finally {
            localDatastore.setLastContentUpdate(System.currentTimeMillis())
        }
    }
}
