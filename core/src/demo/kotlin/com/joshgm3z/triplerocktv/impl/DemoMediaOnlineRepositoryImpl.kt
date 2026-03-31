package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.core.repository.LoadingState
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.MovieMetadata
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
    ) {
    }

    override suspend fun getMovieMetadata(streamId: Int): MovieMetadata {
        return MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            cast = "John Doe, Jane Smith",
            director = "Alice Johnson",
            genre = "Adventure",
        )
    }

    override suspend fun getSeriesDataAndUpdate(streamId: Int) {}
}
