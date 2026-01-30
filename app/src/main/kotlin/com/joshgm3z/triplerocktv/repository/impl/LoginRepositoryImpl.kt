package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.retrofit.XtreamService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor() : LoginRepository {
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
                    onSuccess()
                } else {
                    onError("Invalid username or password")
                }
            } else {
                onError("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            onError("Connection failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

}