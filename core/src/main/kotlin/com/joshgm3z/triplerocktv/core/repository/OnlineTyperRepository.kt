package com.joshgm3z.triplerocktv.core.repository

interface OnlineTyperRepository {
    suspend fun newTypingSessionUrl(): String?
    suspend fun deleteTypingSession()
    fun listenInput(sessionId: String, onInput: (String) -> Unit)
}