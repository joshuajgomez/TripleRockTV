package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor() : LoginRepository {
    override fun tryLogin(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}