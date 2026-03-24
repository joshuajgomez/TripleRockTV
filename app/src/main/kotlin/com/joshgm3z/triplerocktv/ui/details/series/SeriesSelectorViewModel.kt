package com.joshgm3z.triplerocktv.ui.details.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.room.series.Season
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesSelectorUiState(
    val selectedSeasonIndex: Int? = null,
    val selectedEpisodeIndex: Int? = null,
    val seasons: List<Season> = emptyList(),
    val episodes: List<Episode> = emptyList(),
)

@HiltViewModel
class SeriesSelectorViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: MediaLocalRepository,
) : ViewModel() {

    private val seriesId: Int = savedStateHandle.get<Int>("seriesId")
        ?: throw IllegalArgumentException("seriesId is required")

    private val initialSelectedEpisodeId: Int =
        savedStateHandle.get<Int>("initialSelectedEpisodeId")
            ?: throw IllegalArgumentException("initialSelectedEpisodeId is required")

    private val _uiState = MutableStateFlow(SeriesSelectorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val seriesStream = repository.seriesStreamFlow(seriesId).first()
            seriesStream.seasons?.let { seasons ->
                _uiState.update {
                    val selectedSeasonIndex = seasons.getSeasonIndex(initialSelectedEpisodeId)
                    it.copy(
                        seasons = seasons,
                        selectedSeasonIndex = selectedSeasonIndex,
                        selectedEpisodeIndex = initialSelectedEpisodeId,
                        episodes = seasons[selectedSeasonIndex].episodes
                    )
                }
            }
        }
    }

    fun onSeasonSelected(seasonNumber: Int) {
        _uiState.update { it ->
            val selectedSeasonIndex = it.seasons.indexOfFirst { it.number == seasonNumber }
            it.copy(
                selectedSeasonIndex = selectedSeasonIndex,
                selectedEpisodeIndex = null,
                episodes = it.seasons[selectedSeasonIndex].episodes
            )
        }
    }

    private fun List<Season>.getSeasonIndex(episodeId: Int): Int {
        forEachIndexed { index, season ->
            if (season.episodes.any { it.id == episodeId }) return index
        }
        return -1
    }
}