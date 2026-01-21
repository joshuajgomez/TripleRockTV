package com.joshgm3z.triplerocktv.repository

interface LoginRepository {
    fun tryLogin(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )
}
