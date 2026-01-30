package com.joshgm3z.triplerocktv.repository.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface XtreamService {
    @GET("player_api.php")
    suspend fun validateLogin(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<XtreamUserResponse>
}