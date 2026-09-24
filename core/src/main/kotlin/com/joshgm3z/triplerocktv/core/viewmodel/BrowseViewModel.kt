package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.RecentsRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BrowseUiState {
    data class Empty(val streamType: StreamType) : BrowseUiState()
    data object Loading : BrowseUiState()
    data class Error(val message: String) : BrowseUiState()

    data class VideoOnDemandState(
        val favorites: List<StreamData> = emptyList(),
        val recentPlayed: List<StreamData> = emptyList(),
        val favoritesFlow: Flow<List<StreamData>> = emptyFlow(),
        val recentPlayedFlow: Flow<List<StreamData>> = emptyFlow(),
        val newlyAdded: List<StreamData> = emptyList(),
        val categoryMap: Map<String, List<CategoryData>> = emptyMap(),
        val pagingCategoryData: Flow<PagingData<CategoryData>> = emptyFlow(),
    ) : BrowseUiState()

    data class LiveTvState(
        val recentPlayed: List<StreamData> = emptyList(),
        val favorites: List<StreamData> = emptyList(),
        val favoritesFlow: Flow<List<StreamData>> = emptyFlow(),
        val recentPlayedFlow: Flow<List<StreamData>> = emptyFlow(),
        val categoryMap: Map<String, List<CategoryData>> = emptyMap(),
        val pagingCategoryData: Flow<PagingData<CategoryData>> = emptyFlow(),
    ) : BrowseUiState()

    data class SeriesStreamState(
        val recentPlayedEpisodes: List<SeriesStream> = emptyList(),
        val favorites: List<SeriesStream> = emptyList(),
        val favoritesFlow: Flow<List<SeriesStream>> = emptyFlow(),
        val recentPlayedEpisodesFlow: Flow<List<SeriesStream>> = emptyFlow(),
        val pagingCategoryData: Flow<PagingData<CategoryData>> = emptyFlow(),
    ) : BrowseUiState()
}

private val pagingConfig = PagingConfig(
    pageSize = 8,
    enablePlaceholders = false,
    prefetchDistance = 3
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
    private val recentsRepository: RecentsRepository,
) : ViewModel() {

    private val streamType = savedStateHandle.get<StreamType>("streamType")
        ?: throw IllegalArgumentException("streamType not provided")

    private val _uiState = MutableStateFlow<BrowseUiState>(BrowseUiState.Loading)

    val uiState = _uiState.asStateFlow()

    val isBlurSettingEnabled: Boolean = true

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = when (streamType) {
                StreamType.VideoOnDemand -> getVideoOnDemandState()
                StreamType.Series -> getSeriesStreamState()
                StreamType.LiveTV -> getLiveTvState()
                else -> throw IllegalArgumentException("Invalid stream type")
            }
        }
    }

    private suspend fun getSeriesStreamState(): BrowseUiState {
        val uiState = BrowseUiState.SeriesStreamState(
            favoritesFlow = repository.favoritesSeriesFlow(),
            recentPlayedEpisodesFlow = recentsRepository.recentlyPlayedSeriesFlow(),
            pagingCategoryData = Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    repository.fetchPagingCategoryData(StreamType.Series)
                }
            ).flow.cachedIn(viewModelScope),
        )
        val categoriesEmpty = repository.fetchCategories(StreamType.Series).isEmpty()
        return if (categoriesEmpty) BrowseUiState.Empty(StreamType.Series)
        else uiState
    }

    private suspend fun getVideoOnDemandState(): BrowseUiState {
        val uiState = BrowseUiState.VideoOnDemandState(
            favoritesFlow = repository.favoritesFlow(StreamType.VideoOnDemand),
            recentPlayedFlow = recentsRepository.recentlyPlayedStreamDataFlow(StreamType.VideoOnDemand),
            newlyAdded = repository.fetchNewlyAdded(StreamType.VideoOnDemand),
            categoryMap = mutableMapOf<String, List<CategoryData>>().apply {
                listOf("English", "Malayalam", "Hindi", "Tamil").forEach { lang ->
                    val categories = repository.fetchCategoriesByTitleKey(
                        StreamType.VideoOnDemand,
                        lang
                    )
                    if (categories.isNotEmpty()) this[lang] = categories
                }
            },
            pagingCategoryData = Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    repository.fetchPagingCategoryData(StreamType.VideoOnDemand)
                }
            ).flow.cachedIn(viewModelScope)
        )
        val categoriesEmpty = repository.fetchCategories(StreamType.VideoOnDemand).isEmpty()
        return if (categoriesEmpty) BrowseUiState.Empty(StreamType.VideoOnDemand)
        else uiState
    }

    private suspend fun getLiveTvState(): BrowseUiState {
        val uiState = BrowseUiState.LiveTvState(
            favoritesFlow = repository.favoritesFlow(StreamType.LiveTV),
            recentPlayedFlow = recentsRepository.recentlyPlayedStreamDataFlow(StreamType.LiveTV),
            categoryMap = mutableMapOf<String, List<CategoryData>>().apply {
                listOf("English", "Malayalam", "News", "India", "Hindi", "Tamil").forEach { lang ->
                    val categories = repository.fetchCategoriesByTitleKey(
                        StreamType.LiveTV,
                        lang
                    )
                    if (categories.isNotEmpty()) this[lang] = categories
                }
            },
            pagingCategoryData = Pager(
                config = pagingConfig,
                pagingSourceFactory = {
                    repository.fetchPagingCategoryData(StreamType.LiveTV)
                }
            ).flow.cachedIn(viewModelScope),
        )
        val categoriesEmpty = repository.fetchCategories(StreamType.LiveTV).isEmpty()
        return if (categoriesEmpty) BrowseUiState.Empty(StreamType.LiveTV)
        else uiState
    }

    fun removeFromRecentlyPlayed(
        streamId: Int,
        streamType: StreamType
    ) {
        viewModelScope.launch {
            recentsRepository.removeRecentlyPlayed(streamId, streamType)
        }
    }
}
