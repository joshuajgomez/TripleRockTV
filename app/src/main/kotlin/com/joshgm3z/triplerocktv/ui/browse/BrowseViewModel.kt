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
    val myList: List<StreamData> = emptyList(),
    val recentPlayed: List<StreamData> = emptyList(),
    val categoryMap: Map<String, List<CategoryData>> = emptyMap(),
    val seriesCategories: List<SeriesCategory> = emptyList(),
    val epgCategories: List<IptvEpgListing> = emptyList(),
    var errorMessage: String? = null,
    var loading: Boolean = false,
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
        fetchList()
        viewModelScope.launch {
            localDatastore.blurSettingFlow().collectLatest {
                isBlurSettingEnabled = it
            }
        }
    }

    private fun fetchList() {
        _uiState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    categoryMap = mapOf(
                        "All movies" to repository.fetchCategories(StreamType.VideoOnDemand),
                        "English" to repository.fetchCategoriesByTitleKey(
                            StreamType.VideoOnDemand,
                            "English"
                        ),
                        "Malayalam" to repository.fetchCategoriesByTitleKey(
                            StreamType.VideoOnDemand,
                            "Malayalam"
                        ),
                        "Hindi" to repository.fetchCategoriesByTitleKey(
                            StreamType.VideoOnDemand,
                            "Hindi"
                        ),
                        "Tamil" to repository.fetchCategoriesByTitleKey(
                            StreamType.VideoOnDemand,
                            "Tamil"
                        ),
                        "Live TV" to repository.fetchCategories(StreamType.LiveTV),
                    ),
                    epgCategories = repository.fetchEpgListings(),
                    recentPlayed = repository.fetchRecentlyPlayed(),
                    myList = repository.fetchMyList(),
                    loading = false,
                )
            }
        }
    }

    fun onViewResumed() {
        fetchList()
    }
}