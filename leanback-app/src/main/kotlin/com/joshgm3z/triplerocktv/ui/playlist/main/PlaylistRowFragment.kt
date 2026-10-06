package com.joshgm3z.triplerocktv.ui.playlist.main

import android.os.Bundle
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.navigation.fragment.findNavController

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

        val cardPresenter = CardPresenter()

        val xtreamCodesAdapter = ArrayObjectAdapter(cardPresenter)
        xtreamCodesAdapter.add(
            XtreamCode(
                "http://example.com/playlist1",
                "user1",
                "pass1",
                System.currentTimeMillis()
            )
        )
        val header = HeaderItem(0L, "Xtream Codes Playlist")
        rowsAdapter.add(ListRow(header, xtreamCodesAdapter))

        val playlistAdapter = ArrayObjectAdapter(cardPresenter)
        playlistAdapter.add(
            M3u8Playlist(
                "https://iptv-org.github.io/iptv/index.m3u",
                System.currentTimeMillis()
            )
        )
        val header2 = HeaderItem(1L, "M3u8 Playlists")
        rowsAdapter.add(ListRow(header2, playlistAdapter))
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
        val view = android.widget.TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(android.graphics.Color.DKGRAY)
            setPadding(20, 20, 20, 20)
            setTextColor(android.graphics.Color.WHITE)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        (viewHolder.view as android.widget.TextView).text = item.toString()
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources
    }
}