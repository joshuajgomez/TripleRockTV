package com.joshgm3z.triplerocktv.ui.details.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesSelectorUiState(
    val selectedSeasonNumber: Int? = null,
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
            seriesStream.seasons?.filter {
                it.episodes.isNotEmpty()
            }?.let { seasons ->
                _uiState.update { it ->
                    val selectedSeasonNumber = seasons.getSeasonNumber(initialSelectedEpisodeId)
                    val episodes = seasons.first { it.number == selectedSeasonNumber }.episodes
                    it.copy(
                        seasons = seasons,
                        selectedSeasonNumber = selectedSeasonNumber,
                        selectedEpisodeIndex = episodes.indexOfFirst { it.id == initialSelectedEpisodeId },
                        episodes = episodes
                    )
                }
            }
        }
    }

    fun onSeasonSelected(seasonNumber: Int) {
        _uiState.update { it ->
            it.copy(
                selectedSeasonNumber = seasonNumber,
                selectedEpisodeIndex = null,
                episodes = it.seasons.first { it.number == seasonNumber }.episodes
            )
        }
    }

    private fun List<Season>.getSeasonNumber(episodeId: Int): Int {
        forEach { season ->
            if (season.episodes.any { it.id == episodeId }) return season.number
        }
        return -1
    }
}