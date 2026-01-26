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
        onError: (String) -> Unit
    ) {
        MediaLoadingType.entries.forEach { type ->
            repeat(11) { i ->
                onFetch(type, LoadingState(i * 10, LoadingStatus.Ongoing))
                delay(200)
                if (i == 10) onFetch(type, LoadingState(status = LoadingStatus.Complete))
            }
        }
    }
}
