package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.retrofit.XtreamService
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategoryDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStreamsDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodStreamsDao
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val localDataStore: LocalDatastore,
    private val vodStreamsDao: VodStreamsDao,
    private val vodCategoryDao: VodCategoryDao,
    private val liveTvCategoryDao: LiveTvCategoryDao,
    private val liveTvStreamsDao: LiveTvStreamsDao,
    private val seriesCategoryDao: SeriesCategoryDao,
    private val seriesStreamsDao: SeriesStreamsDao,
    private val epgListingDao: EpgListingDao,
) : LoginRepository {
    override suspend fun tryLogin(
        webUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // 1. Ensure URL ends with a slash for Retrofit
            val baseUrl = if (webUrl.endsWith("/")) webUrl else "$webUrl/"

            // 2. Build dynamic Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(XtreamService::class.java)

            // 3. Make the call
            val response = service.validateLogin(username, password)

            if (response.isSuccessful) {
                val body = response.body()
                // Xtream API returns auth = 1 on success
                if (body?.user_info?.auth == 1) {
                    localDataStore.storeCredentials(body, webUrl, password)
                    delay(1000)
                    onSuccess()
                } else {
                    onError("Invalid username or password")
                }
            } else {
                onError("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            delay(1000)
            onError("Connection failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override suspend fun tryLogout(onLogoutComplete: () -> Unit) {
        localDataStore.clearAllData()
        vodStreamsDao.deleteAllStreams()
        vodCategoryDao.deleteAllCategories()
        liveTvCategoryDao.deleteAllCategories()
        liveTvStreamsDao.deleteAllStreams()
        seriesCategoryDao.deleteAllCategories()
        seriesStreamsDao.deleteAllStreams()
        epgListingDao.deleteAllEpgListings()

        delay(1000)
        onLogoutComplete()
    }
}