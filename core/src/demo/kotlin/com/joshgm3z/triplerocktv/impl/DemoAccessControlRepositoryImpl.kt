package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.BuildConfig
import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.core.repository.AccessState
import com.joshgm3z.triplerocktv.core.util.Logger
import javax.inject.Inject

class DemoAccessControlRepositoryImpl
@Inject constructor() : AccessControlRepository {

    override suspend fun getAccessState(username: String?): AccessState {
        return AccessState(enabled = true, reason = "Access disabled for user")
    }

    override suspend fun appUpdateState(): AccessState {
        Logger.debug("current app version name = [${BuildConfig.VERSION_NAME}]")
        return AccessState(
            enabled = true,
            reason = "Update to v2026.5.40 to continue using the app. You are currently on ${BuildConfig.VERSION_NAME}"
        )
    }
}