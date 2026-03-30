package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.repository.LoadingState
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
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

    override suspend fun getMovieDataAndUpdate(
        streamId: Int,
        streamType: StreamType
    ) {}
}
