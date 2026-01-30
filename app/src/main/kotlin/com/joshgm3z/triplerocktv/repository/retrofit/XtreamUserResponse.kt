package com.joshgm3z.triplerocktv.repository.retrofit

data class XtreamUserResponse(
    val user_info: UserInfo? = null,
    val server_info: ServerInfo? = null
) {
    data class UserInfo(
        val auth: Int, // 1 for success, 0 for fail
        val status: String,
        val exp_date: String? = null,
        val username: String
    )

    data class ServerInfo(
        val url: String,
        val port: String
    )
}