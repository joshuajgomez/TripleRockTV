package com.joshgm3z.triplerocktv.ui.playlist.main

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.home.SettingItem
import com.joshgm3z.triplerocktv.ui.home.SettingsItemPresenter
import dagger.hilt.android.AndroidEntryPoint

data class M3u8Playlist(
    val url: String,
    val addedMs: Long,
)

data class XtreamCode(
    val url: String,
    val username: String,
    val password: String,
    val addedMs: Long,
)

@AndroidEntryPoint
class PlaylistFragment : BrowseSupportFragment() {

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
                    is XtreamCode -> PlaylistFragmentDirections.toSplash()
                    is M3u8Playlist -> PlaylistFragmentDirections.toM3u8Fragment(item.url)
                    is SettingItem -> PlaylistFragmentDirections.toAddPlaylistDialog()
                    else -> return@OnItemViewClickedListener
                }.let {
                    findNavController().navigate(it)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = listOf(
            M3u8Playlist(
                "https://iptv-org.github.io/iptv/index.m3u",
                System.currentTimeMillis()
            ),
            /*M3u8Playlist(
                "https://iptv-org.github.io/iptv/index.m3u",
                System.currentTimeMillis()
            ),
            M3u8Playlist(
                "https://iptv-org.github.io/iptv/index.m3u",
                System.currentTimeMillis()
            ),*/
        )
        playlistAdapter.setItems(list, null)
    }
}
