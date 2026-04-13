package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.viewmodel.UserInfo

enum class CompanionLoginState {
    Idle,
    Verifying,
    Success,
    Error,
}

interface CompanionLoginRepository {
    suspend fun newLoginSessionId(): String?
    suspend fun deleteLoginSessionId(sessionId: String)
    fun listenStatus(sessionId: String, onState: (CompanionLoginState) -> Unit)
}