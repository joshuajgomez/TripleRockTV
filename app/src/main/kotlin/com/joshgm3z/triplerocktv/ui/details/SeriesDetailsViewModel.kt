package com.joshgm3z.triplerocktv.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.toTextTime
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesDetailsUiState(
    val episodeLabel: String,
    val episodeTitle: String,
    val episodeId: Int,
    val genre: String,
    val rating: Float,
    val inMyList: Boolean,
    val description: String,
    val duration: String,
    val seasonPoster: String,
    val episodePoster: String,
    val timeLeft: String? = null,
    val progressPercent: Int = 0,
)

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    localDatastore: LocalDatastore,
    private val repository: MediaLocalRepository,
    private val onlineRepository: MediaOnlineRepository,
) : ViewModel() {

    private val seriesId: Int = savedStateHandle.get<Int>("seriesId")
        ?: throw IllegalArgumentException("seriesId is required")

    private val _seriesDetailsUiState = MutableStateFlow<SeriesDetailsUiState?>(null)
    val seriesDetailsUiState = _seriesDetailsUiState.asStateFlow()

    var isBlurSettingEnabled: Boolean = false

    init {
        viewModelScope.launch {
            isBlurSettingEnabled = localDatastore.blurSettingFlow().first()
        }
        fetchStreamDetails(seriesId)
    }

    private fun fetchStreamDetails(seriesId: Int) {
        viewModelScope.launch {
            repository.seriesStreamFlow(seriesId).collectLatest {
                if (it.seasons.isNullOrEmpty()) searchMetadata(it)
                else {
                    val episodeToPlay = it.seasons.first().episodes.first()
                    _seriesDetailsUiState.value = SeriesDetailsUiState(
                        episodeLabel = episodeToPlay.label(),
                        episodeTitle = episodeToPlay.title,
                        episodeId = episodeToPlay.id,
                        genre = it.genre ?: "",
                        rating = episodeToPlay.episodeInfo?.rating.parseToFloat(),
                        inMyList = false,
                        description = episodeToPlay.episodeInfo?.plot ?: "",
                        duration = episodeToPlay.totalDurationMs().toTextTime(),
                        seasonPoster = it.coverImageUrl ?: "",
                        episodePoster = episodeToPlay.episodeInfo?.movie_image ?: "",
                        timeLeft = episodeToPlay.timeRemaining().toTextTime(),
                        progressPercent = episodeToPlay.progressPercent(),
                    )
                }
            }
        }
    }

    private fun Episode.label(): String {
        return "S${season.toString().padStart(2, '0')}" +
                ": E${episode_num.toString().padStart(2, '0')}"
    }

    private fun searchMetadata(seriesStream: SeriesStream) {
        Logger.debug("seriesStream = [${seriesStream}]")
        viewModelScope.launch(Dispatchers.IO) {
            onlineRepository.getSeriesDataAndUpdate(seriesStream.seriesId)
        }
    }

    fun addToMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(seriesId, true)
        }
    }

    fun removeFromMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(seriesId, false)
        }
    }
}