package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.category.BrowseType
import com.joshgm3z.triplerocktv.ui.browse.stream.StreamPresenter
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set a title for the search fragment
        title = "Movies, Series or Live TV"
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is SearchUiState.Initial -> showInitialUi()
                        is SearchUiState.Loading -> showLoadingUi()
                        is SearchUiState.Result -> updateSearchResult(it)
                    }
                }
            }
        }
        setupClickListeners()
    }

    private fun setupClickListeners() = setOnItemViewClickedListener { _, item, _, _ ->
        val action = SearchFragmentDirections.toDetails()
        when (item) {
            is VodStream -> action.apply {
                streamId = item.streamId
                browseType = BrowseType.VideoOnDemand
            }

            is LiveTvStream -> action.apply {
                streamId = item.streamId
                browseType = BrowseType.LiveTV
            }

            is SeriesStream -> action.apply {
                streamId = item.seriesId
                browseType = BrowseType.Series
            }

            else -> return@setOnItemViewClickedListener
        }.let {
            findNavController().navigate(it)
        }
    }


    private fun showLoadingUi() {
        rowsAdapter.clear()
        val header = HeaderItem(0, "Searching")
        rowsAdapter.add(ListRow(header, ArrayObjectAdapter()))
    }

    private fun showInitialUi() {
        rowsAdapter.clear()
        val header = HeaderItem(0, "Start typing to search")
        rowsAdapter.add(ListRow(header, ArrayObjectAdapter()))
    }

    private fun updateSearchResult(result: SearchUiState.Result) {
        Logger.debug("result = [${result}]")
        rowsAdapter.clear()
        when {
            result.isEmpty() -> {
                val header = HeaderItem(0, "No results found for '${result.query}'")
                rowsAdapter.add(ListRow(header, ArrayObjectAdapter()))
            }

            else -> {
                if (result.vodStreams.isNotEmpty()) addResults("Video on demand", result.vodStreams)
                if (result.liveTvStreams.isNotEmpty()) addResults("Live TV", result.liveTvStreams)
                if (result.seriesStreams.isNotEmpty()) addResults("Series", result.seriesStreams)
            }
        }
    }

    private fun addResults(headerText: String, result: List<Any>) {
        val header = HeaderItem(0, "$headerText (${result.size})")
        val adapter = ArrayObjectAdapter(StreamPresenter())
        adapter.addAll(0, result)
        rowsAdapter.add(ListRow(header, adapter))
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        viewModel.onSearchInputChange(newQuery ?: "")
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        // Handle query submission
        return false
    }
}