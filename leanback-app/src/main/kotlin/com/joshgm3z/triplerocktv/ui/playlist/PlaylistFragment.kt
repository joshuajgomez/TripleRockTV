package com.joshgm3z.triplerocktv.ui.playlist

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.joshgm3z.triplerocktv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : Fragment(R.layout.fragment_playlist) {
    private val playlistViewModel by viewModels<PlaylistViewModel>()
}
