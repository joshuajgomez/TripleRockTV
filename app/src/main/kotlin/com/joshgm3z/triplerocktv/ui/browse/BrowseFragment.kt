package com.joshgm3z.triplerocktv.ui.browse

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.joshgm3z.triplerocktv.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowseFragment : BrowseSupportFragment() {

    private val viewModel: BrowseViewModel by viewModels()
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupEventListeners()
        observeViewModel()
        prepareEntranceTransition()
    }

    private lateinit var backgroundManager: BackgroundManager

    private fun setupUI() {
        brandColor = ContextCompat.getColor(requireContext(), R.color.black)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = "3Rock TV"
        // Initialize BackgroundManager
        backgroundManager = BackgroundManager.getInstance(requireActivity())
        backgroundManager.attach(requireActivity().window)

        // Set a solid background color for the entire fragment
        backgroundManager.color = ContextCompat.getColor(requireContext(), R.color.black)


        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, row ->
            // Handle item selection if needed
        }

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, row ->
            // Handle item click if needed
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    updateRows(uiState)
                }
            }
        }
    }

    private fun updateRows(uiState: BrowseUiState) {
        rowsAdapter.clear()

        // Map categories to rows
        uiState.contentMap.forEach { (category, streams) ->
            val header = HeaderItem(category.categoryId.toLong(), category.categoryName)
            val listRowAdapter = ArrayObjectAdapter(StreamPresenter())

            listRowAdapter.addAll(0, streams)

            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        if (uiState.contentMap.isNotEmpty()) {
            startEntranceTransition()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrowseFragment()
    }
}