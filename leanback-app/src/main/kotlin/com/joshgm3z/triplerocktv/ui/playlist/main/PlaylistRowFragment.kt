package com.joshgm3z.triplerocktv.ui.playlist.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewPlaylistCardBinding
import com.joshgm3z.triplerocktv.databinding.ViewSettingCardBinding
import com.joshgm3z.triplerocktv.ui.home.SettingItem
import com.joshgm3z.triplerocktv.ui.home.SettingsItemPresenter

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

class PlaylistRowFragment : RowsSupportFragment() {
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = rowsAdapter

        setupRows()
        setupEventListeners()
    }

    private fun setupRows() {
        rowsAdapter.clear()
        val playlistAdapter = ArrayObjectAdapter(CardPresenter())
        playlistAdapter.add(
            M3u8Playlist(
                "https://iptv-org.github.io/iptv/index.m3u",
                System.currentTimeMillis()
            )
        )
        rowsAdapter.add(ListRow(playlistAdapter))

        val settingsAdapter = ArrayObjectAdapter(SettingsItemPresenter())
        settingsAdapter.add(
            SettingItem(
                title = "Add playlist",
                iconRes = R.drawable.rounded_add_2_24
            )
        )
        rowsAdapter.add(ListRow(settingsAdapter))
    }

    private fun setupEventListeners() {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                when (item) {
                    is XtreamCode -> PlaylistFragmentDirections.toSplash()
                    is M3u8Playlist -> PlaylistFragmentDirections.toM3u8Fragment(item.url)
                    else -> return@OnItemViewClickedListener
                }.let {
                    findNavController().navigate(it)
                }
            }
    }
}

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
        val binding = ViewPlaylistCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = ViewPlaylistCardBinding.bind(viewHolder.view)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources
    }
}