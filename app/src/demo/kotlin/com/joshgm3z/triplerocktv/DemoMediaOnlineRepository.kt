package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class DemoMediaOnlineRepository
@Inject
constructor() : MediaOnlineRepository {
    override suspend fun fetchContent(
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        MediaLoadingType.entries.forEach { type ->
            repeat(10) { i ->
                onFetch(type, LoadingState(i * 10, LoadingStatus.Ongoing))
                delay(200)
            }
            onFetch(type, LoadingState(percent = 100, status = LoadingStatus.Complete))
        }
    }
}
