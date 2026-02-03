package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ObjectAdapter
import com.joshgm3z.triplerocktv.ui.browse.StreamPresenter

class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set a title for the search fragment
        title = "Search"
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        // Handle query text changes
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        // Handle query submission
        return true
    }
}