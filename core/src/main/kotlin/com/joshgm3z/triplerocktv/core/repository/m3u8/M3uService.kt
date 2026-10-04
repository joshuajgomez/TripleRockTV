package com.joshgm3z.triplerocktv.core.repository.m3u8

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface M3uService {
    @GET
    suspend fun downloadPlaylist(@Url url: String): Response<ResponseBody>
}