package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class IptvServiceProvider
@Inject constructor(
    private val localDatastore: LocalDatastore,
) {
    private var iptvService: IptvService? = null
    var username: String = ""
    var password: String = ""

    suspend fun get(): IptvService? {
        Logger.debug("entry")
        if (iptvService == null) {
            localDatastore.getUserInfo()?.let {
                iptvService = getIptvService(it.webUrl)
                username = it.username
                password = it.password
            }
        }
        return iptvService
    }

    private fun getIptvService(serverUrl: String): IptvService {
        Logger.debug("serverUrl = [${serverUrl}]")

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
}