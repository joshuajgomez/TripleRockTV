package com.joshgm3z.triplerocktv.repository

interface LoginRepository {
    suspend fun tryLogin(
        webUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun tryLogout(onLogoutCompleter: () -> Unit) {}
}
