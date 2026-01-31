package com.joshgm3z.triplerocktv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

class MainBrowseFragment : BrowseSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadData()
    }

    private fun setupUI() {
        // Set brand color (background of the side menu)
        brandColor = ContextCompat.getColor(requireContext(), R.color.black)

        // Headers (side menu) behavior
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Optional: Title or Logo in the top-right
        title = "TripleRockTV"
    }

    private fun loadData() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        // 1. Create a Header for the side menu
        val header = HeaderItem(0, "Live TV")

        // 2. Create an Adapter for the items in that row (using your StreamAdapter logic)
        // Note: Leanback uses "Presenters" instead of standard RecyclerView Adapters
        val listRowAdapter = ArrayObjectAdapter(StreamPresenter())
        listRowAdapter.add(StreamEntity.sample())

        // 3. Add the row to the main adapter
        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainBrowseFragment()
    }
}