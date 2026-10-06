package com.joshgm3z.triplerocktv.ui.playlist.m3u8

import androidx.lifecycle.SavedStateHandle
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
class M3u8ViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    playlistRepository: PlaylistRepository,
) : ViewModel() {
    private val _channels = MutableStateFlow<List<M3uChannel>>(emptyList())
    val channels = _channels.asStateFlow()

    private val playlistUrl = savedStateHandle.get<String>("playlistUrl")
        ?: throw IllegalArgumentException("playlistUrl is required")

    val selectedChannel = MutableStateFlow<M3uChannel?>(null)

    init {
        viewModelScope.launch {
            _channels.value = playlistRepository.getChannels(playlistUrl)
        }
    }

}