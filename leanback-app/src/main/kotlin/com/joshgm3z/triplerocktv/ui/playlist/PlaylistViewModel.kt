package com.joshgm3z.triplerocktv.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.m3u8.M3uChannel
import com.joshgm3z.triplerocktv.core.repository.m3u8.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    playlistRepository: PlaylistRepository
) : ViewModel() {
    private val _channels = MutableStateFlow<List<M3uChannel>>(emptyList())
    val channels = _channels.asStateFlow()

    val selectedChannel = MutableStateFlow<M3uChannel?>(null)

    init {
        val samplePlaylist = "https://iptv-org.github.io/iptv/index.m3u"
        viewModelScope.launch {
            _channels.value = playlistRepository.getChannels(samplePlaylist)
        }
    }

}