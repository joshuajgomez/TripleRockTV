package com.joshgm3z.triplerocktv.core.repository

data class AccessState(
    val enabled: Boolean,
    val reason: String,
)

interface AccessControlRepository {
    suspend fun getAccessState(username: String?): AccessState
    suspend fun appUpdateState(): AccessState
}