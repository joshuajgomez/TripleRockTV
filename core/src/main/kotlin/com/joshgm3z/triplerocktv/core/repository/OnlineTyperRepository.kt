package com.joshgm3z.triplerocktv.core.repository

interface OnlineTyperRepository {
    suspend fun newTypingSessionId(): String?
    fun listenInput(sessionId: String, onInput: (String) -> Unit)
}