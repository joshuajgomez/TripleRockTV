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
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.toTextTime
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
    val duration: String? = null,
    val inMyList: Boolean = false,
    val description: String? = null,
    val cast: String? = null,
    val director: String? = null,
    val progressPercent: Int? = null,
    val showMoreEpisodesButton: Boolean = false,
    val subtitleDownloaded: Boolean = false,
    val coverImage: String? = null,
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

    var isBlurSettingEnabled: Boolean = false

    private var streamId: Int? = null

    init {
        viewModelScope.launch {
            isBlurSettingEnabled = localDatastore.blurSettingFlow().first()
        }
        val streamId = savedStateHandle.get<Int>("streamId")
        val streamType = savedStateHandle.get<StreamType>("streamType")
        if (streamId != null && streamType != null)
            fetchStreamDetails(streamId, streamType)
    }

    private fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        this.streamId = streamId
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
        viewModelScope.launch {
            repository.streamDataFlow(streamId, streamType).collectLatest {
                _uiState.value = DetailsUiState(
                    streamType = streamType,
                    title = it.name,
                    rating = it.rating,
                )
                if (it.movieMetadata?.description.isNullOrEmpty())
                    searchMetadata(it)
                else _uiState.update { uiState ->
                    uiState?.copy(
                        duration = it.movieMetadata.totalDurationMs.toTextTime(),
                        subtitle = it.movieMetadata.genre,
                        description = it.movieMetadata.description,
                        cast = it.movieMetadata.cast.withPrefix("Cast: "),
                        director = it.movieMetadata.director.withPrefix("Director: "),
                        progressPercent = if (it.progressPercent() > 0) it.progressPercent() else null,
                        inMyList = it.inMyList,
                        coverImage = it.movieMetadata.backPosterUrl,
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
                    coverImage = seriesStream.backdropUrl,
                )
                if (seriesStream.seasons.isNullOrEmpty())
                    searchSeriesMetadata(seriesStream)
                else _uiState.update {
                    val episodeToPlay = seriesStream.seasons.getEpisodeToPlay()
                    it?.copy(
                        episodeLabel = episodeToPlay.label(),
                        subtitle = episodeToPlay.title,
                        episodeId = episodeToPlay.id,
                        rating = episodeToPlay.episodeInfo?.rating.parseToFloat(),
                        description = episodeToPlay.episodeInfo?.plot,
                        cast = seriesStream.cast.withPrefix("Cast: "),
                        director = seriesStream.director.withPrefix("Director: "),
                        duration = episodeToPlay.totalDurationMs().toTextTime(),
                        progressPercent = if (episodeToPlay.progressPercent() > 0) episodeToPlay.progressPercent() else null,
                        showMoreEpisodesButton = true,
                        inMyList = seriesStream.inMyList,
                    )
                }
            }
        }
    }

    private fun String?.withPrefix(text: String): String {
        if (this.isNullOrEmpty()) return ""
        return "$text$this"
    }

    private fun searchMetadata(streamData: StreamData) {
        Logger.debug("streamData = [${streamData}]")
        viewModelScope.launch(Dispatchers.IO) {
            onlineRepository.getMovieDataAndUpdate(streamData.streamId, streamData.streamType)
        }
    }

    fun addToMyList(streamType: StreamType) {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            if (streamType == StreamType.Series) repository.updateMyListSeries(streamId!!, true)
            else repository.updateMyList(streamId!!, true)
        }
    }

    fun removeFromMyList(streamType: StreamType) {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            if (streamType == StreamType.Series) repository.updateMyListSeries(streamId!!, false)
            else repository.updateMyList(streamId!!, false)
        }
    }

    private fun List<Season>.getEpisodeToPlay(): Episode {
        val allEpisodes = mutableListOf<Episode>()
        forEach { season -> allEpisodes.addAll(season.episodes) }
        allEpisodes.sortByDescending { it.lastPlayed }
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