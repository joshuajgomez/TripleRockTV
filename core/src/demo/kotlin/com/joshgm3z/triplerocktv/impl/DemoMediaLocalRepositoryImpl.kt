package com.joshgm3z.triplerocktv.impl

import com.joshgm3z.triplerocktv.DemoData
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
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

    override suspend fun fetchCategoriesByTitleKey(
        streamType: StreamType,
        titleKey: String
    ): List<CategoryData> = when (streamType) {
        StreamType.VideoOnDemand -> DemoData.sampleVodCategory()
        StreamType.LiveTV -> DemoData.getSampleLiveTvCategories()
        else -> emptyList()
    }.filter { it.categoryName.contains(titleKey, ignoreCase = true) }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        DemoData.getSampleIptvEpgListings()

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
    ): StreamData = DemoData.sampleVodStreams.first { it.streamId == streamId }

    override suspend fun fetchEpisode(
        episodeId: Int,
        seriesId: Int
    ): Episode = DemoData.getSampleSeriesStreams()
        .first { it.seriesId == seriesId }.seasons?.first()?.episodes!!.first()

    override fun streamDataFlow(
        streamId: Int,
        streamType: StreamType
    ): Flow<StreamData> = flow {
        DemoData.allStreams.firstOrNull {
            it.streamId == streamId
        }?.let {
            emit(it)
        }
    }

    override fun seriesStreamFlow(streamId: Int): Flow<SeriesStream> = flow {
        DemoData.getSampleSeriesStreams().firstOrNull {
            it.seriesId == streamId
        }?.let {
            emit(it)
        }
    }

    override suspend fun isContentEmpty(): Boolean = false

    override suspend fun fetchRecentlyPlayedStreamData(streamType: StreamType): List<StreamData> {
        return DemoData.sampleVodStreams
    }

    override suspend fun fetchRecentlyPlayedSeries(): List<SeriesStream> {
        return DemoData.getSampleSeriesStreams()
    }

    override suspend fun fetchMyList(streamType: StreamType): List<StreamData> {
        return DemoData.sampleVodStreams.filter { it.inMyList }
    }

    override suspend fun fetchNewlyAdded(streamType: StreamType): List<StreamData> {
        return DemoData.sampleVodStreams.filter { it.inMyList }
    }

    override suspend fun fetchMyListSeries(): List<SeriesStream> {
        return DemoData.getSampleSeriesStreams()
    }

    override suspend fun updatePlayedDuration(
        streamId: Int,
        positionMs: Long,
        streamType: StreamType
    ) {}

    override suspend fun updateLastPlayedTimestamp(
        streamId: Int,
        streamType: StreamType,
        timeStamp: Long
    ) {}

    override suspend fun updateEpisodeLastPlayedTimestamp(
        episodeId: Int,
        seriesId: Int,
        timeStamp: Long
    ) {}

    override suspend fun updateEpisodePlayedDuration(
        episodeId: Int,
        seriesId: Int,
        positionMs: Long
    ) {}

    override suspend fun updateMyList(streamId: Int, add: Boolean) {}

    override suspend fun updateMyListSeries(seriesId: Int, add: Boolean) {}

    override suspend fun updateSelectedSubtitle(
        streamId: Int,
        language: String,
        title: String,
        url: String?
    ) {
    }

}