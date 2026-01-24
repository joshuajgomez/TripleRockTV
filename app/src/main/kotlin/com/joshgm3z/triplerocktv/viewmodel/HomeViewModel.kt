package com.joshgm3z.triplerocktv.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryEntity> = emptyList(),
    val mediaList: List<MediaData> = emptyList(),
)

@HiltViewModel
class HomeViewModel
@Inject constructor(
    repository: MediaLocalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        Log.i(TAG, "entry")
        viewModelScope.launch {
            repository.fetchCategories(
                onSuccess = { categories ->
                    Log.i(TAG, "fetchCategories.onSuccess $categories")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories
                        )
                    }
                },
                onError = {},
            )
        }
    }
}