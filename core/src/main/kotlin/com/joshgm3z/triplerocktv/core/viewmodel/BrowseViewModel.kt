package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
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

    data class StreamDataState(
        val myList: List<StreamData> = emptyList(),
        val recentPlayed: List<StreamData> = emptyList(),
        val newlyAdded: List<StreamData> = emptyList(),
        val categoryMap: Map<String, List<CategoryData>> = emptyMap(),
    ) : BrowseUiState()

    data class SeriesStreamState(
        val recentPlayedEpisodes: List<SeriesStream> = emptyList(),
        val myList: List<SeriesStream> = emptyList(),
        val seriesCategories: List<CategoryData> = emptyList(),
    ) : BrowseUiState()
}

@HiltViewModel
class BrowseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
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
        recentPlayedEpisodes = repository.fetchRecentlyPlayedSeries(),
        seriesCategories = repository.fetchCategories(StreamType.Series),
    )

    private suspend fun getVideoOnDemandState() = BrowseUiState.StreamDataState(
        myList = repository.fetchMyList(StreamType.VideoOnDemand),
        recentPlayed = repository.fetchRecentlyPlayedStreamData(StreamType.VideoOnDemand),
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

    private suspend fun getLiveTvState() = BrowseUiState.StreamDataState(
        myList = repository.fetchMyList(StreamType.LiveTV),
        recentPlayed = repository.fetchRecentlyPlayedStreamData(StreamType.LiveTV),
        newlyAdded = repository.fetchNewlyAdded(StreamType.LiveTV),
        categoryMap = mapOf(
            "Live TV" to repository.fetchCategories(StreamType.LiveTV)
        )
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