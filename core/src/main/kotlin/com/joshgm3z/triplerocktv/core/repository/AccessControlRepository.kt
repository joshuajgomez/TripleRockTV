package com.joshgm3z.triplerocktv.core.repository

data class AccessState(
    val enabled: Boolean,
    val reason: String,
)

interface AccessControlRepository {
    fun getAccessState(username: String?): AccessState
    fun appUpdateState(): AccessState
}