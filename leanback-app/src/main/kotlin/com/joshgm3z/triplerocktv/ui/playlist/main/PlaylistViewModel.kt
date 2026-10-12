package com.joshgm3z.triplerocktv.ui.playlist.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.PlaylistRepository
import com.joshgm3z.triplerocktv.core.repository.room.playlist.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel
@Inject constructor(
    repository: PlaylistRepository
) : ViewModel() {
    private val _playlistsState = MutableStateFlow(emptyList<Playlist>())
    val playlistsState = _playlistsState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.playlistsFlow().collectLatest { playlists ->
                _playlistsState.value = playlists
            }
        }
    }
}
