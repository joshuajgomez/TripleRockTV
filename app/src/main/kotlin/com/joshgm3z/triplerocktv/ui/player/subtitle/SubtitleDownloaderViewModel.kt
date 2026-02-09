package com.joshgm3z.triplerocktv.ui.player.subtitle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubtitleUiState(
    val downloadedSubtitleList: List<SubtitleData>? = null,
    val defaultSubtitleList: List<SubtitleData>? = null
)

@HiltViewModel
class SubtitleDownloaderViewModel
@Inject
constructor(
    private val subtitleRepository: SubtitleRepository
) : ViewModel() {

    private val _subtitleUiState = MutableStateFlow(SubtitleUiState())
    val subtitleUiState = _subtitleUiState.asStateFlow()

    fun onFindClicked(query: String) {
        viewModelScope.launch {
            _subtitleUiState.update {
                it.copy(downloadedSubtitleList = subtitleRepository.findSubtitles(query))
            }
        }
    }

    fun onSubtitleClicked(subtitleData: SubtitleData) {
        viewModelScope.launch {
            val url = subtitleRepository.getSubtitleUrl(subtitleData.fileId)
            _subtitleUiState.update {
                val list = it.defaultSubtitleList?.toMutableList() ?: return@launch
                it.copy(defaultSubtitleList = list.apply { add(subtitleData.apply { copy(url = url) }) })
            }
        }
    }

    fun onDefaultSubtitlesFound(list: List<SubtitleData>) {
        _subtitleUiState.update {
            it.copy(defaultSubtitleList = list)
        }
    }
}
