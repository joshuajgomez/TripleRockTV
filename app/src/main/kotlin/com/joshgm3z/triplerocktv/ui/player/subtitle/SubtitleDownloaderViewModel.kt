package com.joshgm3z.triplerocktv.ui.player.subtitle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubtitleUiState(
    val defaultSubtitleList: List<SubtitleData>? = null,
    val downloadedSubtitleList: List<SubtitleData>? = null,
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
        Logger.debug("subtitleData = [${subtitleData}]")
        viewModelScope.launch {
            val url = subtitleRepository.getSubtitleUrl(subtitleData.fileId)
            Logger.debug("url = [${url}]")
            _subtitleUiState.update {
                it.copy(defaultSubtitleList = listOf(subtitleData.copy(url = url)))
            }
        }
    }

    fun onDefaultSubtitlesFound(list: List<SubtitleData>) {
        _subtitleUiState.update {
            it.copy(defaultSubtitleList = list)
        }
    }
}
