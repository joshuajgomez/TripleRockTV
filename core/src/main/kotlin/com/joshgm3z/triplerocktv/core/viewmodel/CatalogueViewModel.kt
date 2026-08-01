package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CatalogueUiState(
    val categoryName: String,
    val streamType: StreamType,
    val count: Int,
) {
    class VideoOnDemand(
        categoryName: String = "Video on demand",
        count: Int = 0,
        val pagingStreams: Flow<PagingData<StreamData>> = emptyFlow(),
    ) : CatalogueUiState(categoryName, StreamType.VideoOnDemand, count)

    class LiveTv(
        categoryName: String = "Live Tv",
        count: Int = 0,
        val userInfo: UserInfo? = null,
        val streamDataList: Flow<List<StreamData>> = emptyFlow(),
    ) : CatalogueUiState(categoryName, StreamType.LiveTV, count)

    class Series(
        categoryName: String = "Series",
        count: Int = 0,
        val pagingStreams: Flow<PagingData<SeriesStream>> = emptyFlow(),
    ) : CatalogueUiState(categoryName, StreamType.Series, count)
}

private val pagingConfig = PagingConfig(
    pageSize = 8,
    enablePlaceholders = false,
    prefetchDistance = 3
)

@HiltViewModel
class CatalogueViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
    private val onlineRepository: MediaOnlineRepository,
    private val localDatastore: LocalDatastore
) : ViewModel() {

    private val categoryId = savedStateHandle.get<Int>("categoryId")
        ?: throw IllegalStateException("categoryId not found")

    private val streamType = savedStateHandle.get<StreamType>("streamType")
        ?: throw IllegalStateException("streamType not found")

    private val _uiState = MutableStateFlow<CatalogueUiState?>(null)
    val uiState = _uiState.asStateFlow()

    init {
        fetchStreams(categoryId, streamType)
    }

    private fun fetchStreams(categoryId: Int, streamType: StreamType) {
        Logger.debug("categoryId = [${categoryId}], browseType = [${streamType}]")
        viewModelScope.launch(Dispatchers.IO) {
            val categoryName = repository.getCategory(categoryId)?.categoryName
                ?: throw Exception("Category not found")
            _uiState.value = when (streamType) {
                StreamType.VideoOnDemand -> CatalogueUiState.VideoOnDemand(
                    categoryName = categoryName,
                    pagingStreams = Pager(
                        config = pagingConfig,
                        pagingSourceFactory = {
                            repository.fetchPagingStreamsOfCategory(
                                categoryId,
                                streamType
                            )
                        }
                    ).flow.cachedIn(viewModelScope),
                    count = repository.getStreamsCountOfCategory(categoryId, streamType)
                )

                StreamType.LiveTV -> CatalogueUiState.LiveTv(
                    categoryName = categoryName,
                    userInfo = localDatastore.getUserInfo(),
                    streamDataList = repository.fetchLiveStreamsOfCategoryFlow(categoryId),
                    count = repository.getStreamsCountOfCategory(categoryId, streamType)
                )

                StreamType.Series -> CatalogueUiState.Series(
                    categoryName = categoryName,
                    pagingStreams = Pager(
                        config = pagingConfig,
                        pagingSourceFactory = {
                            repository.fetchPagingSeriesStreamsOfCategory(categoryId)
                        }
                    ).flow.cachedIn(viewModelScope),
                    count = repository.getStreamsCountOfCategory(categoryId, streamType)
                )
            }
        }
    }

    suspend fun fetchMetadata(streamId: Int): MovieMetadata? {
        return onlineRepository.getMovieMetadata(streamId)
    }

    fun updateFavorites(streamId: Int, add: Boolean) {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(streamId, streamType, add)
        }
    }
}