package com.joshgm3z.triplerocktv.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.BrowseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val title: String = "",
    val imageUrl: String? = null,
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    localDatastore: LocalDatastore,
    private val repository: MediaLocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState = _uiState.asStateFlow()

    var isBlurSettingEnabled: Boolean = false

    init {
        viewModelScope.launch {
            isBlurSettingEnabled = localDatastore.blurSettingFlow().first()
        }
    }

    fun fetchStreamDetails(streamId: Int, browseType: BrowseType) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.fetchStream(streamId, browseType)) {
                is VodStream -> _uiState.update {
                    it.copy(
                        title = result.name,
                        imageUrl = result.streamIcon
                    )
                }

                is LiveTvStream -> _uiState.update {
                    it.copy(
                        title = result.name,
                        imageUrl = result.streamIcon
                    )
                }

                is SeriesStream -> _uiState.update {
                    it.copy(
                        title = result.name,
                        imageUrl = result.cover
                    )
                }
            }
        }
    }
}
