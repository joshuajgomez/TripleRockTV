package com.joshgm3z.triplerocktv.ui.browse.newbrowse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainBrowseUiState {
    data object Loading : MainBrowseUiState()
    data class Error(val message: String) : MainBrowseUiState()

    data class VideoOnDemandState(
        val myList: List<StreamData> = emptyList(),
        val recentPlayed: List<StreamData> = emptyList(),
        val categoryMap: Map<String, List<CategoryData>> = emptyMap(),
    ) : MainBrowseUiState()

    data class SeriesStreamState(
        val recentPlayedEpisodes: List<Episode> = emptyList(),
        val myList: List<SeriesStream> = emptyList(),
        val seriesCategories: List<SeriesCategory> = emptyList(),
    ) : MainBrowseUiState()
}

@HiltViewModel
class NewBrowseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
) : ViewModel() {

    private val streamType = savedStateHandle.get<StreamType>("streamType")
        ?: throw IllegalArgumentException("streamType not provided")

    private val _uiState = MutableStateFlow<MainBrowseUiState>(MainBrowseUiState.Loading)

    val uiState = _uiState.asStateFlow()

    val isBlurSettingEnabled: Boolean = true

    init {
        Logger.debug("found streamType = $streamType")
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = when (streamType) {
                StreamType.VideoOnDemand -> getVideoOnDemandState()
                StreamType.Series -> getSeriesStreamState()
                else -> throw IllegalArgumentException("Invalid stream type")
            }
        }
    }

    private suspend fun getSeriesStreamState() = MainBrowseUiState.SeriesStreamState(
        myList = emptyList(),
        recentPlayedEpisodes = emptyList(),
    )

    private suspend fun getVideoOnDemandState() = MainBrowseUiState.VideoOnDemandState(
        myList = repository.fetchMyList(),
        recentPlayed = repository.fetchRecentlyPlayed(),
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
}