package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.DemoData
import com.joshgm3z.triplerocktv.repository.SearchRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.StreamData
import javax.inject.Inject

class DemoSearchRepositoryImpl
@Inject constructor() : SearchRepository {
    override suspend fun searchStreamByName(
        name: String,
        streamType: StreamType
    ): List<StreamData> = when (streamType) {
        StreamType.VideoOnDemand -> DemoData.sampleVodStreams.filter {
            it.name.contains(name, ignoreCase = true)
        }

        StreamType.LiveTV -> DemoData.sampleLiveStreams.filter {
            it.name.contains(name, ignoreCase = true)
        }

        else -> emptyList()
    }

    override suspend fun addSearchText(searchText: String) {}

    override suspend fun getSearchTextList(): List<String> {
        return listOf("The Matrix", "Breaking Bad", "Game of Thrones", "Stranger Things")
    }
}