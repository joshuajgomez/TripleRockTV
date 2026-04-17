package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.BuildConfig
import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.core.repository.AccessState
import com.joshgm3z.triplerocktv.core.repository.OnlineTyperRepository
import com.joshgm3z.triplerocktv.core.util.Logger
import javax.inject.Inject

class DemoTyperRepositoryImpl
@Inject constructor() : OnlineTyperRepository {
    override suspend fun newTypingSessionUrl(): String {
        return ""
    }

    override suspend fun deleteTypingSession() {}

    override fun listenInput(onInput: (String) -> Unit) {}

}