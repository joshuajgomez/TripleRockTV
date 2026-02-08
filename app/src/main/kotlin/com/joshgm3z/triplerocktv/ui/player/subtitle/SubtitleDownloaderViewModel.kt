package com.joshgm3z.triplerocktv.ui.player.subtitle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubtitleDownloaderViewModel
@Inject
constructor(
    private val subtitleRepository: SubtitleRepository
) : ViewModel() {

    private val _subtitleList = MutableStateFlow<List<SubtitleData>?>(null)
    val subtitleList = _subtitleList.asStateFlow()

    fun onFindClicked(query: String) {
        viewModelScope.launch {
            _subtitleList.value = subtitleRepository.findSubtitles(query)
        }
    }
}
