package com.joshgm3z.triplerocktv.ui.browse.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseUiState(
    val vodCategories: List<VodCategory> = emptyList(),
    val seriesCategories: List<SeriesCategory> = emptyList(),
    val liveTvCategories: List<LiveTvCategory> = emptyList(),
    val epgCategories: List<IptvEpgListing> = emptyList(),
    var errorMessage: String? = null,
)

enum class BrowseType {
    Home,
    VideoOnDemand,
    LiveTV,
    EPG,
    Series,
}

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class BrowseViewModel
@Inject constructor(
    private val repository: MediaLocalRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    vodCategories = repository.fetchCategories(BrowseType.VideoOnDemand) as List<VodCategory>,
                    seriesCategories = repository.fetchCategories(BrowseType.Series) as List<SeriesCategory>,
                    liveTvCategories = repository.fetchCategories(BrowseType.LiveTV) as List<LiveTvCategory>,
                    epgCategories = repository.fetchEpgListings(),
                )
            }
        }
    }
}