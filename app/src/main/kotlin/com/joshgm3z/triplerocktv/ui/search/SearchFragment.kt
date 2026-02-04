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
import com.joshgm3z.triplerocktv.ui.browse.SearchPresenter
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
        title = "Movies"
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
        if (result.list.isEmpty()) {
            val header = HeaderItem(0, "No results found for '${result.query}'")
            rowsAdapter.add(ListRow(header, ArrayObjectAdapter()))
        } else {
            val header = HeaderItem(0, "Search results for '${result.query}'")
            val adapter = ArrayObjectAdapter(SearchPresenter())
            adapter.addAll(0, result.list)
            rowsAdapter.add(ListRow(header, adapter))
        }
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
        return true
    }
}