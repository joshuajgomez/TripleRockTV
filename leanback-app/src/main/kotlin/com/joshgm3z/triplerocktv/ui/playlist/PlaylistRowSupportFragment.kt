package com.joshgm3z.triplerocktv.ui.playlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.core.repository.m3u8.M3uChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class PlaylistRowSupportFragment : VerticalGridSupportFragment() {

    private val playlistViewModel by viewModels<PlaylistViewModel>({ requireParentFragment() })

    @Inject
    lateinit var playlistPresenter: PlaylistPresenter

    lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRowFragment()
    }

    private val diffCallback = object : DiffCallback<M3uChannel>() {
        override fun areItemsTheSame(
            channel1: M3uChannel,
            channel2: M3uChannel
        ): Boolean {
            return channel1.url == channel2.url
        }

        override fun areContentsTheSame(
            channel1: M3uChannel,
            channel2: M3uChannel
        ): Boolean {
            return channel1.url == channel2.url
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            playlistViewModel.channels.collectLatest {
                rowsAdapter.setItems(it, diffCallback)
            }
        }
    }

    private fun initRowFragment() {
        gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 5
        }
        rowsAdapter = ArrayObjectAdapter(playlistPresenter)
        adapter = rowsAdapter
        onItemViewClickedListener = clickListener
    }

    private val clickListener = OnItemViewClickedListener { _, item, _, _ ->
        val m3uChannel = item as M3uChannel
        val direction = PlaylistFragmentDirections.toPlayback()
//        findNavController().navigate(direction)
    }
}