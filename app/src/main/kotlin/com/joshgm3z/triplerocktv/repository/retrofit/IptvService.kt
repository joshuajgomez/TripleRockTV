package com.joshgm3z.triplerocktv.repository.retrofit

import com.joshgm3z.triplerocktv.repository.data.IptvCategory
import com.joshgm3z.triplerocktv.repository.data.IptvEpgResponse
import com.joshgm3z.triplerocktv.repository.data.IptvSeries
import com.joshgm3z.triplerocktv.repository.data.IptvStream
import com.joshgm3z.triplerocktv.repository.data.SeriesDetailResponse
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
        @Query("category_id") categoryId: Int,
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
    suspend fun getSeriesCategories(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_categories"
    ): List<IptvCategory>

    @GET("player_api.php")
    suspend fun getSeries(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("category_id") categoryId: Int? = null,
        @Query("action") action: String = "get_series"
    ): List<IptvSeries>

    @GET("player_api.php")
    suspend fun getSeriesDetails(
        @Query("username") user: String,
        @Query("password") pass: String,
        @Query("action") action: String = "get_series_info",
        @Query("series_id") seriesId: Int
    ): SeriesDetailResponse

    @GET("player_api.php")
    suspend fun getShortEpg(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("stream_id") streamId: Int,
        @Query("action") action: String = "get_short_epg"
    ): IptvEpgResponse
}
