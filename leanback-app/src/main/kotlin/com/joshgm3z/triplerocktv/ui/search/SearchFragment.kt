package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.viewmodel.MediaSyncViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.SearchUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SearchViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentSearchBinding
import com.joshgm3z.triplerocktv.ui.common.diffCallback
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.util.fragmentScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    private val mediaSyncViewModel: MediaSyncViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaSyncViewModel.bindService(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        initViews()

        lifecycleScope.launch {
            mediaSyncViewModel.syncState.collectLatest {
                binding.tvSyncStatus.text = it
            }
        }

        lifecycleScope.launch {
            viewModel.hintsState.collectLatest {
                binding.rvHints.adapter = SimpleTextAdapter(it) { text ->
                    binding.keyboardView.text = text
                }
            }
        }
    }

    private fun initViews() {
        binding.etInput.setText(viewModel.initialSearchText)
        binding.keyboardView.text = viewModel.initialSearchText ?: ""
        binding.rvHints.isFocusable = false
        binding.keyboardView.listener = object : KeyboardViewListener {
            override fun onTextChange(text: String) {
                binding.etInput.setText(text)
                viewModel.onSearchInputChange(text)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSyncViewModel.unbindService(requireContext())
    }
}

@AndroidEntryPoint
class SearchResultRow : RowsSupportFragment() {

    @Inject
    lateinit var streamPresenter: StreamPresenter

    private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        )
    )

    private lateinit var listRowAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRows()
        listenToState()
    }

    private fun initRows() {
        listRowAdapter = ArrayObjectAdapter(streamPresenter)
        adapter = rowsAdapter
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            onSearchResultClick(item)
        }
    }

    private fun listenToState() {
        fragmentScope {
            viewModel.uiState.collectLatest {
                when (it) {
                    is SearchUiState.Initial -> setRow("Recently added", it.initialStreams)
                    is SearchUiState.Result -> setRow("Search result", it.list)
                    is SearchUiState.Loading -> setRow("Searching")
                    is SearchUiState.NoResult -> setRow("No results found")
                    else -> rowsAdapter.clear()
                }
            }
        }
    }

    private fun setRow(title: String, streams: List<Any> = emptyList()) {
        rowsAdapter.clear()
        val header = HeaderItem(0, title)
        rowsAdapter.add(ListRow(header, listRowAdapter))
        listRowAdapter.setItems(streams, diffCallback)
    }

    private fun onSearchResultClick(it: Any) {
        when (it) {
            is StreamData -> when (it.streamType) {
                StreamType.VideoOnDemand -> SearchFragmentDirections.toDetails().apply {
                    streamId = it.streamId
                    streamType = it.streamType
                }

                else -> SearchFragmentDirections.toLiveTvCatalogue().apply {
                    categoryId = it.categoryId
                    selectedStreamId = it.streamId
                }
            }

            is SeriesStream -> SearchFragmentDirections.toDetails().apply {
                streamId = it.seriesId
                streamType = StreamType.Series
            }

            else -> return
        }.let { action ->
            findNavController().navigate(action)
        }
    }
}
