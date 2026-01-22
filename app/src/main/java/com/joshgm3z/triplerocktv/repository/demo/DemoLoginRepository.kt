package com.joshgm3z.triplerocktv.repository.demo

import com.joshgm3z.triplerocktv.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DemoLoginRepository
@Inject
constructor(
    private val scope: CoroutineScope
) : LoginRepository {
    override fun tryLogin(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            delay(2000)
            if (username.isEmpty() || password.isEmpty())
                onError("Username or password cannot be empty")
            else
                onSuccess()
        }
    }
}