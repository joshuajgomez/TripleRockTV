package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import kotlinx.coroutines.delay
import javax.inject.Inject

class DemoMediaOnlineRepositoryImpl
@Inject
constructor() : MediaOnlineRepository {
    override suspend fun fetchContent(
        onFetch: (StreamType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        StreamType.entries.forEach { type ->
            repeat(10) { i ->
                onFetch(type, LoadingState(i * 10, LoadingStatus.Ongoing))
                delay(200)
            }
            onFetch(type, LoadingState(percent = 100, status = LoadingStatus.Complete))
        }
    }
}
