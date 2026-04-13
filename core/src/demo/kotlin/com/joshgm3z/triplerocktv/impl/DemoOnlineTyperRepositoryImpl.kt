package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.repository.OnlineTyperRepository
import javax.inject.Inject

class DemoOnlineTyperRepositoryImpl
@Inject constructor() : OnlineTyperRepository {
    override suspend fun newTypingSessionId(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTypingSessionId(sessionId: String) {
        TODO("Not yet implemented")
    }

    override fun listenInput(sessionId: String, onInput: (String) -> Unit) {
        TODO("Not yet implemented")
    }
}