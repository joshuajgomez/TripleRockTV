package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.retrofit.Secrets
import kotlinx.coroutines.delay
import javax.inject.Inject

class DemoLoginRepositoryImpl
@Inject
constructor() : LoginRepository {
    override suspend fun tryLogin(
        webUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        delay(2000)
        when {
            username.isEmpty() || password.isEmpty() || webUrl.isEmpty() -> onError("Please fill in all fields")
            username != Secrets.username -> onError("Incorrect credentials")
            password != Secrets.password -> onError("Incorrect credentials")
            webUrl != Secrets.webUrl -> onError("Incorrect credentials")
            else -> onSuccess()
        }
    }

    override suspend fun tryLogout(onLogoutCompleter: () -> Unit) {
        delay(1000)
        onLogoutCompleter()
    }
}