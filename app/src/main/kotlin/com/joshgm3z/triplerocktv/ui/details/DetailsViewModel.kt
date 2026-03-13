package com.joshgm3z.triplerocktv.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.MovieInfoRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    localDatastore: LocalDatastore,
    private val repository: MediaLocalRepository,
    private val movieInfoRepository: MovieInfoRepository,
) : ViewModel() {

    private val _streamData = MutableStateFlow<StreamData?>(null)
    val streamData = _streamData.asStateFlow()

    var isBlurSettingEnabled: Boolean = false

    private var streamId: Int? = null

    private var metadataSearched: Boolean = false

    init {
        viewModelScope.launch {
            isBlurSettingEnabled = localDatastore.blurSettingFlow().first()
        }
    }

    fun fetchStreamDetails(streamId: Int, streamType: StreamType) {
        this.streamId = streamId
        viewModelScope.launch {
            repository.streamDataFlow(streamId, streamType).collectLatest { sd ->
                _streamData.value = sd
                if (!metadataSearched) searchMetadata(sd.name)
            }
        }
    }

    private fun searchMetadata(name: String) {
        Logger.debug("name = [${name}]")
        metadataSearched = true
        viewModelScope.launch {
            movieInfoRepository.searchMovieData(name.trimMovieName())?.let { movieData ->
                _streamData.update {
                    it?.copy(
                        description = movieData.description,
                        backPosterUrl = movieData.backPosterUrl
                    )
                }
            }
        }
    }

    fun addToMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(streamId!!, true)
        }
    }

    fun removeFromMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(streamId!!, false)
        }
    }
}

fun String.trimMovieName(): String {
    return replace(Regex("[\\(\\[].*"), "")
        // Removes extra whitespace
        .trim()
}