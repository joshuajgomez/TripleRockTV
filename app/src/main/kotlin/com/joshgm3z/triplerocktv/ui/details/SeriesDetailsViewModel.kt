package com.joshgm3z.triplerocktv.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
    localDatastore: LocalDatastore,
    private val repository: MediaLocalRepository,
    private val onlineRepository: MediaOnlineRepository,
) : ViewModel() {

    private val _seriesStream = MutableStateFlow<SeriesStream?>(null)
    val seriesStream = _seriesStream.asStateFlow()

    var isBlurSettingEnabled: Boolean = false

    private var seriesId: Int? = null

    init {
        viewModelScope.launch {
            isBlurSettingEnabled = localDatastore.blurSettingFlow().first()
        }
    }

    fun fetchStreamDetails(streamId: Int) {
        this.seriesId = streamId
        viewModelScope.launch {
            repository.seriesStreamFlow(streamId).collectLatest {
                _seriesStream.value = it
                if (it.seasons.isNullOrEmpty()) searchMetadata(it)
            }
        }
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
            repository.updateMyList(seriesId!!, true)
        }
    }

    fun removeFromMyList() {
        Logger.entry()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMyList(seriesId!!, false)
        }
    }
}