package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.helper.SeriesFetcher
import com.joshgm3z.triplerocktv.core.repository.impl.helper.OnlineDataFetcher
import com.joshgm3z.triplerocktv.core.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata
import com.joshgm3z.triplerocktv.core.util.Logger
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
) : MediaOnlineRepository {
    companion object {
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
        }
    }

    private fun getIptvService(serverUrl: String): IptvService {
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

    override suspend fun updateAllCategories() {
        onlineDataFetcher.fetchContent(StreamType.VideoOnDemand)
        onlineDataFetcher.fetchContent(StreamType.LiveTV)
        seriesFetcher.fetchContent()
    }

    override suspend fun getMovieDataAndUpdate(streamId: Int): MovieMetadata? {
        if (iptvService == null) {
            fetchIptvService()
        }
        return onlineDataFetcher.getMovieDataAndUpdate(streamId)
    }

    override suspend fun getMovieMetadata(streamId: Int): MovieMetadata? {
        Logger.debug("streamId = [${streamId}]")
        if (iptvService == null) {
            fetchIptvService()
        }
        return onlineDataFetcher.getMovieData(streamId)
    }

    override suspend fun getSeriesDataAndUpdate(streamId: Int) {
        seriesFetcher.getSeriesDataAndUpdate(streamId)
    }

    override suspend fun getShortEpgListing(
        streamId: Int
    ): List<IptvEpgListing> {
        return iptvService?.getShortEpg(
            username = username,
            password = password,
            streamId = streamId,
        )?.epgListings ?: emptyList()
    }

    override suspend fun fetchStreams(
        streamType: StreamType,
        categoryId: Int
    ) {
        if (iptvService == null) {
            fetchIptvService()
        }
        if (streamType == StreamType.Series) seriesFetcher.updateSeriesOfCategory(categoryId)
        else onlineDataFetcher.updateStreamDataList(categoryId, streamType)
    }
}
