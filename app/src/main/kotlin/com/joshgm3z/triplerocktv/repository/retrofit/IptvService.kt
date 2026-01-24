package com.joshgm3z.triplerocktv.repository.retrofit

import com.joshgm3z.triplerocktv.repository.data.IptvCategory
import com.joshgm3z.triplerocktv.repository.data.IptvSeries
import com.joshgm3z.triplerocktv.repository.data.IptvStream
import retrofit2.http.GET
import retrofit2.http.Query

interface IptvService {

    @GET("player_api.php")
    suspend fun getLiveCategories(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_categories"
    ): List<IptvCategory>

    @GET("player_api.php")
    suspend fun getLiveStreams(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("category_id") categoryId: String,
        @Query("action") action: String = "get_live_streams"
    ): List<IptvStream>

    @GET("player_api.php")
    suspend fun getVodCategories(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_categories"
    ): List<IptvCategory>

    @GET("player_api.php")
    suspend fun getVodStreams(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("category_id") categoryId: Int,
        @Query("action") action: String = "get_vod_streams",
    ): List<IptvStream>

    @GET("player_api.php")
    suspend fun getSeries(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series"
    ): List<IptvSeries>
}
