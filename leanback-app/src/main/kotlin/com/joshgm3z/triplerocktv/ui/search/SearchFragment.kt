package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.viewmodel.SearchUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SearchViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentSearchBinding
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.util.GlideUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    private lateinit var streamAdapter: StreamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        streamAdapter = StreamAdapter(glideUtil) { onSearchResultClick(it) }
    }

    private fun onSearchResultClick(it: Any) {
        var text = ""
        when (it) {
            is StreamData -> SearchFragmentDirections.toDetails().apply {
                text = it.name
                streamId = it.streamId
                streamType = it.streamType
            }

            is SeriesStream -> SearchFragmentDirections.toSeriesDetails().apply {
                text = it.name
                seriesId = it.seriesId
            }

            else -> return
        }.let { action ->
            viewModel.saveSearchHint(text)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                binding.rvHints.adapter = SimpleTextAdapter(it.searchHints) { text ->
                    binding.keyboardView.text = text
                }
                when (it.searchUiState) {
                    is SearchUiState.Initial -> showInitialUi()

                    is SearchUiState.Loading -> showLoadingUi()

                    is SearchUiState.Result -> updateSearchResult(it.searchUiState as SearchUiState.Result)
                }
            }
        }
    }

    private fun showLoadingUi() {
        streamAdapter.items = emptyList()
        binding.tvStatus.text = "Searching"
    }

    private fun showInitialUi() {
        streamAdapter.items = emptyList()
        binding.tvStatus.text = ""
        binding.rvSearchList.isFocusable = false
        binding.rvHints.isFocusable = false
    }

    private fun initViews() {
        binding.keyboardView.listener = object : KeyboardViewListener {
            override fun onTextChange(text: String) {
                binding.etInput.setText(text)
                viewModel.onSearchInputChange(text)
            }
        }
        binding.rvSearchList.adapter = streamAdapter
    }

    private fun updateSearchResult(result: SearchUiState.Result) {
        val results = result.streamDataList + result.seriesStreams
        binding.tvStatus.text = when {
            results.isEmpty() -> "No results found"
            else -> ""
        }
        streamAdapter.items = results
    }
}
