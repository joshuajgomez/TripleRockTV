package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.BuildConfig
import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.core.repository.AccessState
import com.joshgm3z.triplerocktv.core.util.FirebaseConfig
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.getVersionCode
import java.lang.NumberFormatException
import javax.inject.Inject

class AccessControlRepositoryImpl
@Inject constructor(
    private val firebaseConfig: FirebaseConfig
) : AccessControlRepository {

    override fun getAccessState(username: String?): AccessState {
        Logger.debug("username = [${username}]")

        val globalAccess = firebaseConfig.getObject<AccessState>("global_access")
            ?: AccessState(
                enabled = true,
                reason = "No restrictions found"
            )
        if (!globalAccess.enabled) return globalAccess

        val bannedUsers = firebaseConfig.getObject<List<String>>("banned_users") ?: emptyList()
        if (bannedUsers.contains(username)) return AccessState(
            enabled = false,
            reason = "User $username is banned"
        )

        return AccessState(enabled = true, reason = "No restrictions found")
    }

    override fun appUpdateState(): AccessState {
        val forcedMinAppversion = firebaseConfig.getObject<String>("forced_min_app_version")
            ?: return AccessState(enabled = true, reason = "No forced min version found")

        val currentAppVersion = BuildConfig.VERSION_NAME
        Logger.debug("currentAppVersion = [$currentAppVersion]")

        return if (currentAppVersion.isOlderThan(forcedMinAppversion)) AccessState(
            enabled = false,
            reason = "Update app to continue"
        ) else AccessState(
            enabled = true,
            reason = "Current version greater than forced min version"
        )
    }

}

fun String.isOlderThan(version: String): Boolean {
    return try {
        this.getVersionCode() < version.getVersionCode()
    } catch (e: NumberFormatException) {
        Logger.error(e.message.toString())
        false
    }
}