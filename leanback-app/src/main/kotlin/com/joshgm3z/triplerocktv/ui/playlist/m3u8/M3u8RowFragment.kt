package com.joshgm3z.triplerocktv.ui.playlist.m3u8

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.m3u8.M3uChannel
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class M3u8RowFragment : VerticalGridSupportFragment() {

    private val m3U8ViewModel: M3u8ViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    @Inject
    lateinit var m3U8ListPresenter: M3u8ListPresenter

    lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().setBackground(requireContext().getBackgroundColor())
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
            m3U8ViewModel.channels.collectLatest {
                rowsAdapter.setItems(it, diffCallback)
            }
        }
    }

    private fun initRowFragment() {
        gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 1
        }
        rowsAdapter = ArrayObjectAdapter(m3U8ListPresenter)
        adapter = rowsAdapter
        onItemViewClickedListener = clickListener
        setOnItemViewSelectedListener(selectedListener)
    }

    private val selectedListener = OnItemViewSelectedListener { _, item, _, _ ->
        val m3uChannel = item as? M3uChannel
        m3U8ViewModel.selectedChannel.value = m3uChannel
    }

    private val clickListener = OnItemViewClickedListener { _, item, _, _ ->
        val direction = M3u8FragmentDirections.toM3u8Player()
        findNavController().navigate(direction)
    }
}