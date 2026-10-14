package com.joshgm3z.triplerocktv.ui.playlist.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.playlist.Playlist
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistType
import com.joshgm3z.triplerocktv.ui.home.SettingItem
import com.joshgm3z.triplerocktv.ui.home.SettingsItemPresenter
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : BrowseSupportFragment() {

    private val viewModel by viewModels<PlaylistViewModel>()

    private val playlistAdapter = ArrayObjectAdapter(PlaylistPresenter())

    private val settingsAdapter = ArrayObjectAdapter(SettingsItemPresenter())

    private val rowsAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_NONE,
            false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().setBackground(requireContext().getBackgroundColor())

        headersState = HEADERS_DISABLED
        adapter = rowsAdapter
        setupRows()
        setupEventListeners()
    }

    private fun setupRows() {
        rowsAdapter.add(ListRow(playlistAdapter))
        rowsAdapter.add(ListRow(settingsAdapter))
        settingsAdapter.add(
            SettingItem(
                title = "Add playlist",
                iconRes = R.drawable.rounded_add_2_24
            )
        )
    }

    private fun setupEventListeners() {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                when (item) {
                    is Playlist -> when (item.type) {
                        PlaylistType.Xtream -> PlaylistFragmentDirections.toSplash()
                        else -> PlaylistFragmentDirections.toM3u8Fragment(item.url)
                    }

                    is SettingItem -> PlaylistFragmentDirections.toChoosePlaylistDialog()
                    else -> return@OnItemViewClickedListener
                }.let {
                    findNavController().navigate(it)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.playlistsState.collectLatest { playlists ->
                playlistAdapter.setItems(playlists, null)
            }
        }
    }
}
