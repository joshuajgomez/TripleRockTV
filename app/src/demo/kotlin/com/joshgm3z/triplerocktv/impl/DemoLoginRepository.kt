package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.repository.LoginRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class DemoLoginRepository
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
        if (username.isEmpty() || password.isEmpty() || webUrl.isEmpty())
            onError("Please fill in all fields")
        else
            onSuccess()
    }

    override suspend fun tryLogout(onLogoutCompleter: () -> Unit) {
        delay(1000)
        onLogoutCompleter()
    }
}