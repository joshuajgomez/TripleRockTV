package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.R
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeItem(
    val title: String,
    val iconRes: Int,
)

@HiltViewModel
class HomeViewModel
@Inject constructor(
    repository: MediaLocalRepository,
) : ViewModel() {

    private val _homeListState = MutableStateFlow<List<HomeItem>>(emptyList())
    val homeListState = _homeListState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = arrayListOf<HomeItem>()
            repository.fetchCategories(StreamType.VideoOnDemand).let {
                if (it.isNotEmpty()) categories.add(
                    HomeItem(
                        "Video on demand",
                        R.drawable.movie_avd
                    )
                )
            }
            repository.fetchCategories(StreamType.Series).let {
                if (it.isNotEmpty()) categories.add(
                    HomeItem(
                        "Series",
                        R.drawable.series_avd
                    )
                )
            }
            repository.fetchCategories(StreamType.LiveTV).let {
                if (it.isNotEmpty()) categories.add(
                    HomeItem(
                        "Live TV",
                        R.drawable.livetv_avd
                    )
                )
            }

            _homeListState.value = categories
        }
    }
}