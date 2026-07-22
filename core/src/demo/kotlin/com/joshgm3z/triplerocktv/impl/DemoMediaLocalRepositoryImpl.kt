package com.joshgm3z.triplerocktv.impl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.joshgm3z.triplerocktv.DemoData
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

    override suspend fun getCategory(categoryId: Int): CategoryData? {
        return DemoData.sampleVodCategory().firstOrNull()
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

    override fun fetchLiveStreamsOfCategoryFlow(categoryId: Int): Flow<List<StreamData>> {
        return emptyFlow()
    }

    override fun fetchPagingStreamsOfCategory(
        categoryId: Int,
        streamType: StreamType
    ): PagingSource<Int, StreamData> {
        return object : PagingSource<Int, StreamData>() {
            override fun getRefreshKey(state: PagingState<Int, StreamData>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StreamData> {
                return LoadResult.Page(data = DemoData.sampleVodStreams, prevKey = null, nextKey = null)
            }
        }
    }

    override fun fetchPagingCategoryData(streamType: StreamType): PagingSource<Int, CategoryData> {
        return object : PagingSource<Int, CategoryData>() {
            override fun getRefreshKey(state: PagingState<Int, CategoryData>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryData> {
                return LoadResult.Page(data = DemoData.sampleVodCategory(), prevKey = null, nextKey = null)
            }
        }
    }

    override fun fetchPagingSeriesStreamsOfCategory(categoryId: Int): PagingSource<Int, SeriesStream> {
        return object : PagingSource<Int, SeriesStream>() {
            override fun getRefreshKey(state: PagingState<Int, SeriesStream>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SeriesStream> {
                return LoadResult.Page(data = DemoData.getSampleSeriesStreams(), prevKey = null, nextKey = null)
            }
        }
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

    override suspend fun streamDataFlow(
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
        DemoData.getSampleSeriesStreams().firstOrNull()?.let {
            emit(it)
        }
    }

    override suspend fun isContentEmpty(): Boolean = false

    override suspend fun fetchFavorites(streamType: StreamType): List<StreamData> {
        return DemoData.sampleVodStreams.filter { it.favorite }
    }

    override suspend fun fetchNewlyAdded(streamType: StreamType): List<StreamData> {
        return DemoData.sampleVodStreams.filter { it.favorite }
    }

    override suspend fun fetchFavoritesSeries(): List<SeriesStream> {
        return DemoData.getSampleSeriesStreams()
    }

    override suspend fun updateFavorites(
        streamId: Int,
        streamType: StreamType,
        add: Boolean
    ): Boolean {
        return true
    }

    override suspend fun updateSelectedSubtitle(
        streamId: Int,
        language: String,
        title: String,
        url: String?
    ) {
    }

    override suspend fun numberOfFiles(type: StreamType): Int {
        return when (type) {
            StreamType.VideoOnDemand -> 12000
            else -> 3000
        }
    }

}