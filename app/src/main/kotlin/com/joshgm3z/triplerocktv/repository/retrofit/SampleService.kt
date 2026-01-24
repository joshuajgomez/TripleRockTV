package com.joshgm3z.triplerocktv.repository.retrofit

import com.joshgm3z.triplerocktv.repository.data.SampleRequest
import com.joshgm3z.triplerocktv.repository.data.SampleResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SampleService {
    @POST("posts")
    suspend fun createPost(@Body request: SampleRequest): SampleResponse
}
