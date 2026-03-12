package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.DemoData
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DemoMediaLocalRepositoryImpl
@Inject
constructor() : MediaLocalRepository {
    override suspend fun fetchCategories(
        streamType: StreamType
    ): List<CategoryData> = when (streamType) {
        StreamType.VideoOnDemand -> DemoData.sampleVodCategory()
        StreamType.LiveTV -> DemoData.getSampleLiveTvCategories()
        else -> emptyList()
    }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        DemoData.getSampleIptvEpgListings()

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

    override suspend fun fetchStreamsOfCategory(
        categoryId: Int,
        streamType: StreamType
    ): List<StreamData> = when (streamType) {
        StreamType.VideoOnDemand -> DemoData.sampleVodStreams.filter { it.categoryId == categoryId }
        StreamType.LiveTV -> DemoData.sampleLiveStreams.filter { it.categoryId == categoryId }
        else -> emptyList()
    }

    override suspend fun fetchStream(
        streamId: Int,
        streamType: StreamType
    ): StreamData? = DemoData.allStreams.firstOrNull {
        when (it) {
            is StreamData -> it.streamId == streamId
//            is SeriesStream -> it.seriesId == streamId
            else -> false
        }
    }

    override fun streamDataFlow(
        streamId: Int,
        streamType: StreamType
    ): Flow<StreamData> = flow {
        DemoData.allStreams.firstOrNull {
            when (it) {
                is StreamData -> it.streamId == streamId
//            is SeriesStream -> it.seriesId == streamId
                else -> false
            }
        }?.let {
            emit(it)
        }
    }

    override suspend fun isContentEmpty(): Boolean = false

    override suspend fun fetchRecentlyPlayed(): List<StreamData> {
        return DemoData.sampleVodStreams.subList(0, 2) +
                DemoData.sampleLiveStreams.subList(0, 1)

    }

    override suspend fun fetchMyList(): List<StreamData> {
        return DemoData.sampleVodStreams.filter { it.inMyList }
    }

    override suspend fun updatePlayedDuration(streamId: Int, positionMs: Long) {}

    override suspend fun updateLastPlayedTimestamp(streamId: Int) {}

    override suspend fun updateTotalDuration(streamId: Int, totalDurationMs: Long) {}
    override suspend fun updateMyList(streamId: Int, add: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSelectedSubtitle(
        streamId: Int,
        language: String,
        title: String,
        url: String?
    ) {
    }

}