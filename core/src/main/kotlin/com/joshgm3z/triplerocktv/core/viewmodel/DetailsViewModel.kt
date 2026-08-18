package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.ifNullOrEmpty
import com.joshgm3z.triplerocktv.core.util.toTextTime
import com.joshgm3z.triplerocktv.core.util.withPrefix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.forEach

data class DetailsUiState(
    val streamType: StreamType,
    val title: String,
    val rating: Float? = null,
    val subtitle: String? = null,
    val episodeLabel: String? = null,
    val episodeId: Int? = null,
    val noOfSeasons: Int? = null,
    val duration: String? = null,
    val favorite: Boolean = false,
    val description: String? = null,
    val cast: String? = null,
    val director: String? = null,
    val progressPercent: Int? = null,
    val showMoreEpisodesButton: Boolean = false,
    val subtitleDownloaded: Boolean = false,
    val coverImage: String? = null,
    val showButtons: Boolean = false,
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    localDatastore: LocalDatastore,
    savedStateHandle: SavedStateHandle,
    private val repository: MediaLocalRepository,
    private val onlineRepository: MediaOnlineRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState?>(null)
    val uiState = _uiState.asStateFlow()

    val streamId = savedStateHandle.get<Int>("streamId")
        ?: throw IllegalArgumentException("streamId is required")

    val streamType = savedStateHandle.get<StreamType>("streamType")
        ?: throw IllegalArgumentException("streamType is required")

    init {
        fetchStreamDetails(streamId, streamType)
    }

    private fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        if (streamType == StreamType.VideoOnDemand) {
            fetchStreamData(streamId, streamType)
        } else if (streamType == StreamType.Series) {
            fetchSeries(streamId)
        }
    }

    private fun fetchStreamData(
        streamId: Int,
        streamType: StreamType
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.streamDataFlow(streamId, streamType).collectLatest {
                _uiState.value = DetailsUiState(
                    streamType = streamType,
                    title = it.name,
                    rating = it.rating,
                )
                if (it.movieMetadata == null) searchMetadata(it)
                else _uiState.update { uiState ->
                    uiState?.copy(
                        showButtons = true,
                        duration = it.movieMetadata.totalDurationMs.toTextTime(),
                        subtitle = it.movieMetadata.genre,
                        description = it.movieMetadata.description.withPrefix(""),
                        cast = it.movieMetadata.cast.withPrefix("Cast: "),
                        director = it.movieMetadata.director.withPrefix("Director: "),
                        progressPercent = if (it.progressPercent() > 0) it.progressPercent() else null,
                        favorite = it.favorite,
                        coverImage = it.movieMetadata.backPosterUrl.ifNullOrEmpty(it.streamIcon),
                        subtitleDownloaded = !it.subtitleUrl.isNullOrEmpty()
                    )
                }
            }
        }
    }

    private fun fetchSeries(seriesId: Int) {
        viewModelScope.launch {
            repository.seriesStreamFlow(seriesId).collectLatest { seriesStream ->
                _uiState.value = DetailsUiState(
                    streamType = StreamType.Series,
                    title = seriesStream.name,
                    coverImage = seriesStream.backdropUrl.ifNullOrEmpty(
                        seriesStream.coverImageUrl
                    ),
                )
                if (seriesStream.seasons.isNullOrEmpty())
                    searchSeriesMetadata(seriesStream)
                else _uiState.update {
                    val episodeToPlay = seriesStream.seasons.getEpisodeToPlay()
                    it?.copy(
                        showButtons = true,
                        episodeLabel = episodeToPlay.label(),
                        subtitle = episodeToPlay.title,
                        episodeId = episodeToPlay.id,
                        rating = episodeToPlay.episodeInfo?.rating.parseToFloat(),
                        description = episodeToPlay.episodeInfo?.plot.withPrefix(""),
                        cast = seriesStream.cast.withPrefix("Cast: "),
                        director = seriesStream.director.withPrefix("Director: "),
                        duration = episodeToPlay.totalDurationMs().toTextTime(),
                        progressPercent = if (episodeToPlay.progressPercent() > 0) episodeToPlay.progressPercent() else null,
                        showMoreEpisodesButton = true,
                        favorite = seriesStream.favorite,
                        noOfSeasons = seriesStream.seasons.size
                    )
                }
            }
        }
    }

    private fun searchMetadata(streamData: StreamData) {
        Logger.debug("streamData = [${streamData}]")
        viewModelScope.launch(Dispatchers.IO) {
            onlineRepository.getMovieDataAndUpdate(streamData.streamId)
        }
    }

    fun updateMyList(add: Boolean) {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(streamId, streamType, add)
        }
    }

    private fun List<Season>.getEpisodeToPlay(): Episode {
        val allEpisodes = mutableListOf<Episode>()
        forEach { season -> allEpisodes.addAll(season.episodes) }
        allEpisodes.sortByDescending { it.recentlyPlayed?.added }
        return allEpisodes.first()
    }

    private fun Episode.label(): String = "S$season: E$episode_num"

    private fun searchSeriesMetadata(seriesStream: SeriesStream) {
        Logger.debug("seriesStream = [${seriesStream}]")
        viewModelScope.launch(Dispatchers.IO) {
            onlineRepository.getSeriesDataAndUpdate(seriesStream.seriesId)
        }
    }
}

fun String.trimMovieName(): String {
    return replace(Regex("[\\(\\[].*"), "")
        // Removes extra whitespace
        .trim()
}