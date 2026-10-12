package com.joshgm3z.triplerocktv.core.repository

interface LoginRepository {
    suspend fun tryLogin(
        webUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun validLogin(
        webUrl: String,
        username: String,
        password: String,
    ): Boolean

    suspend fun addIfNotExist()

    suspend fun tryLogout()
}
