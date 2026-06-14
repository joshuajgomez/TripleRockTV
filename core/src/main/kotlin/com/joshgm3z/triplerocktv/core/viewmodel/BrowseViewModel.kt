package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.RecentsRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BrowseUiState {
    data object Loading : BrowseUiState()
    data class Error(val message: String) : BrowseUiState()

    data class VideoOnDemandState(
        val myList: List<StreamData> = emptyList(),
        val recentPlayed: List<StreamData> = emptyList(),
        val newlyAdded: List<StreamData> = emptyList(),
        val categoryMap: Map<String, List<CategoryData>> = emptyMap(),
    ) : BrowseUiState()

    data class LiveTState(
        val recentPlayed: List<StreamData> = emptyList(),
        val myList: List<StreamData> = emptyList(),
        val liveTvMap: Map<CategoryData, List<StreamData>> = emptyMap(),
    ) : BrowseUiState()

    data class SeriesStreamState(
        val recentPlayedEpisodes: List<SeriesStream> = emptyList(),
        val myList: List<SeriesStream> = emptyList(),
        val seriesMap: Map<CategoryData, List<SeriesStream>> = emptyMap(),
    ) : BrowseUiState()
}

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
        Logger.debug("found streamType = $streamType")
    }

    private suspend fun getSeriesStreamState() = BrowseUiState.SeriesStreamState(
        myList = repository.fetchMyListSeries(),
        recentPlayedEpisodes = recentsRepository.fetchRecentlyPlayedSeries(),
        seriesMap = repository.fetchCategories(StreamType.Series).associateWith { category ->
            repository.fetchStreamsOfCategory(
                category.categoryId,
                StreamType.Series
            ) as List<SeriesStream>
        },
    )

    private suspend fun getVideoOnDemandState() = BrowseUiState.VideoOnDemandState(
        myList = repository.fetchMyList(StreamType.VideoOnDemand),
        recentPlayed = recentsRepository.fetchRecentlyPlayedStreamData(StreamType.VideoOnDemand),
        newlyAdded = repository.fetchNewlyAdded(StreamType.VideoOnDemand),
        categoryMap = mapOf(
            "All movies" to repository.fetchCategories(StreamType.VideoOnDemand),
            "English" to repository.fetchCategoriesByTitleKey(
                StreamType.VideoOnDemand,
                "English"
            ),
            "Malayalam" to repository.fetchCategoriesByTitleKey(
                StreamType.VideoOnDemand,
                "Malayalam"
            ),
            "Hindi" to repository.fetchCategoriesByTitleKey(
                StreamType.VideoOnDemand,
                "Hindi"
            ),
            "Tamil" to repository.fetchCategoriesByTitleKey(
                StreamType.VideoOnDemand,
                "Tamil"
            ),
        )
    )

    private suspend fun getLiveTvState() = BrowseUiState.LiveTState(
        recentPlayed = recentsRepository.fetchRecentlyPlayedStreamData(StreamType.LiveTV),
        myList = repository.fetchMyList(StreamType.LiveTV),
        liveTvMap = repository.fetchCategories(StreamType.LiveTV).associateWith { category ->
            repository.fetchStreamsOfCategory(
                category.categoryId,
                StreamType.LiveTV
            ) as List<StreamData>
        },
    )

    fun onViewResume() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = when (streamType) {
                StreamType.VideoOnDemand -> getVideoOnDemandState()
                StreamType.Series -> getSeriesStreamState()
                StreamType.LiveTV -> getLiveTvState()
                else -> throw IllegalArgumentException("Invalid stream type")
            }
        }
    }
}