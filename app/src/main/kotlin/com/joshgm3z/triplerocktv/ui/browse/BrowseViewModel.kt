package com.joshgm3z.triplerocktv.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseUiState(
    val recentPlayed: List<StreamData> = emptyList(),
    val vodCategories: List<CategoryData> = emptyList(),
    val liveTvCategories: List<CategoryData> = emptyList(),
    val seriesCategories: List<SeriesCategory> = emptyList(),
    val epgCategories: List<IptvEpgListing> = emptyList(),
    var errorMessage: String? = null,
)

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class BrowseViewModel
@Inject constructor(
    localDatastore: LocalDatastore,
    private val repository: MediaLocalRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState = _uiState.asStateFlow()

    var isBlurSettingEnabled: Boolean = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    vodCategories = repository.fetchCategories(StreamType.VideoOnDemand),
                    liveTvCategories = repository.fetchCategories(StreamType.LiveTV),
                    epgCategories = repository.fetchEpgListings(),
                )
            }
        }
        viewModelScope.launch {
            localDatastore.blurSettingFlow().collectLatest {
                isBlurSettingEnabled = it
            }
        }
    }

    fun updateRecentPlayed() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    recentPlayed = repository.fetchRecentlyPlayed(),
                )
            }
        }
    }
}