package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.core.repository.AccessState
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreHelper
import com.joshgm3z.triplerocktv.core.util.Logger
import javax.inject.Inject

class AccessControlRepositoryImpl
@Inject constructor(
    private val firestoreHelper: FirestoreHelper
) : AccessControlRepository {

    override suspend fun getAccessState(username: String?): AccessState {
        Logger.debug("username = [${username}]")

        val map = firestoreHelper.getDataMap(
            "access_control",
            "global_access"
        )

        if (map == null) return AccessState(enabled = true, reason = "No restrictions found")


        if (map.containsKey("global_access_enabled") && !(map["global_access_enabled"] as Boolean)) {
            val reason = if (!map.containsKey("global_access_reason")) "Reason unknown"
            else map["global_access_reason"] as String
            return AccessState(enabled = false, reason = reason)
        }

        val bannedUsers = firestoreHelper.getDataMap(
            "access_control",
            "banned_users"
        )
        if (bannedUsers != null && bannedUsers.containsKey(username)) {
            var message = "User $username is banned"
            val reason = bannedUsers[username] as String
            if (reason.isNotEmpty()) message = "$message. Reason: $reason"
            return AccessState(
                enabled = false,
                reason = message
            )
        }

        return AccessState(enabled = true, reason = "No restrictions found")
    }

    override suspend fun appUpdateState(): AccessState {
        return AccessState(enabled = true, reason = "Update to v202")
    }
}