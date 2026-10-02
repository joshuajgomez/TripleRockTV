package com.joshgm3z.triplerocktv.core.viewmodel

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
)

@HiltViewModel
class EpisodeSelectorViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: MediaLocalRepository,
) : ViewModel() {

    val seriesId: Int = savedStateHandle.get<Int>("seriesId")
        ?: throw IllegalArgumentException("seriesId is required")

    private val initialSelectedEpisodeId: Int =
        savedStateHandle.get<Int>("initialSelectedEpisodeId")
            ?: throw IllegalArgumentException("initialSelectedEpisodeId is required")

    private val _uiState = MutableStateFlow(SeriesSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private val _episodesFlow = MutableStateFlow<List<Episode>>(emptyList())
    val episodesFlow = _episodesFlow.asStateFlow()

    private var defaultCoverImageUrl: String? = null

    init {
        viewModelScope.launch {
            val seriesStream = repository.seriesStreamFlow(seriesId).first()
            defaultCoverImageUrl = seriesStream.coverImageUrl
            seriesStream.seasons?.filter {
                it.episodes.isNotEmpty()
            }?.let { seasons ->
                val selectedSeasonNumber = seasons.getSeasonNumber(initialSelectedEpisodeId)
                val episodes = seasons.getEpisodesOfSeason(selectedSeasonNumber)

                _episodesFlow.value = episodes
                _uiState.update { it ->
                    it.copy(
                        seasons = seasons,
                        selectedSeasonNumber = selectedSeasonNumber,
                        selectedEpisodeIndex = episodes.indexOfFirst { it.id == initialSelectedEpisodeId },
                    )
                }
            }
        }
    }

    fun onSeasonSelected(seasonNumber: Int) {
        _episodesFlow.value = _uiState.value.seasons.getEpisodesOfSeason(seasonNumber)
    }

    private fun List<Season>.getEpisodesOfSeason(seasonNumber: Int) = this
        .first { it.number == seasonNumber }
        .episodes
        .replaceMissingPoster(defaultCoverImageUrl)

    private fun List<Season>.getSeasonNumber(episodeId: Int): Int {
        forEach { season ->
            if (season.episodes.any { it.id == episodeId }) return season.number
        }
        return -1
    }

    fun List<Episode>.replaceMissingPoster(poster: String?): List<Episode> = map {
        if (it.episodeInfo?.movie_image.isNullOrEmpty())
            it.copy(
                episodeInfo = it.episodeInfo?.copy(
                    movie_image = poster
                )
            )
        else it
    }
}