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
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.viewmodel.SearchUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty

@AndroidEntryPoint
class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

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
                    when (it.searchUiState) {
                        is SearchUiState.Initial -> showInitialUi(it.searchHints)
                        is SearchUiState.Loading -> showLoadingUi()
                        is SearchUiState.Result -> updateSearchResult(it.searchUiState as SearchUiState.Result)
                    }
                }
            }
        }
        setupClickListeners()
    }

    private fun setupClickListeners() = setOnItemViewClickedListener { _, item, _, _ ->
        val action = SearchFragmentDirections.toDetails()
        var itemName = ""
        when (item) {
            is StreamData -> action.apply {
                itemName = item.name
                streamId = item.streamId
                streamType = item.streamType
            }

            is SeriesStream -> action.apply {
                itemName = item.name
                streamId = item.seriesId
                streamType = StreamType.Series
            }

            is String -> {
                setSearchQuery(item, true)
                return@setOnItemViewClickedListener
            }

            else -> return@setOnItemViewClickedListener
        }.let {
            viewModel.saveSearchHint(itemName)
            findNavController().navigate(it)
        }
    }


    private fun showLoadingUi() {
        rowsAdapter.clear()
        val header = HeaderItem(0, "Searching")
        rowsAdapter.add(ListRow(header, ArrayObjectAdapter()))
    }

    private fun showInitialUi(searchHints: List<String>) {
        Logger.debug("searchHints = [${searchHints}]")
        rowsAdapter.clear()
        val header = HeaderItem(0, "Start typing to search")
        val adapter = ArrayObjectAdapter(SimpleTextPresenter(R.drawable.ic_history))
        adapter.addAll(0, searchHints)
        rowsAdapter.add(ListRow(header, adapter))
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
                val vodStreamDataList = result.streamDataList.filter {
                    it.streamType == StreamType.VideoOnDemand
                }
                if (vodStreamDataList.isNotEmpty()) addResults(
                    "Video on demand",
                    vodStreamDataList
                )
                val liveTvStreamDataList = result.streamDataList.filter {
                    it.streamType == StreamType.LiveTV
                }
                if (liveTvStreamDataList.isNotEmpty()) addResults(
                    "Live TV", liveTvStreamDataList
                )
                if (result.seriesStreams.isNotEmpty()) addResults("Series", result.seriesStreams)
            }
        }
    }

    private fun addResults(headerText: String, result: List<Any>) {
        val header = HeaderItem(0, "$headerText (${result.size})")
        val adapter = ArrayObjectAdapter(streamPresenter)
        adapter.addAll(0, result)
        rowsAdapter.add(ListRow(header, adapter))
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        Logger.debug("newQuery = [${newQuery}]")
        viewModel.onSearchInputChange(newQuery?.trim() ?: "")
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Logger.debug("query = [${query}]")
        query?.let {
            viewModel.saveSearchHint(it)
        }
        return false
    }
}