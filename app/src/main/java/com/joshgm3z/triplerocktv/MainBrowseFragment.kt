package com.joshgm3z.triplerocktv

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
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
import com.joshgm3z.triplerocktv.ui.home.HomeViewModel
import com.joshgm3z.triplerocktv.ui.home.HomeUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainBrowseFragment : BrowseSupportFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupEventListeners()
        observeViewModel()
        prepareEntranceTransition()
    }

    private fun setupUI() {
        brandColor = ContextCompat.getColor(requireContext(), R.color.black)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = "TripleRockTV"
        
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

    private fun updateRows(uiState: HomeUiState) {
        rowsAdapter.clear()

        // Map categories to rows
        uiState.categoryEntities.forEachIndexed { index, category ->
            val header = HeaderItem(index.toLong(), category.categoryName)
            val listRowAdapter = ArrayObjectAdapter(StreamPresenter())
            
            // If this is the selected category, show its streams
            if (uiState.selectedCategoryEntity?.categoryId == category.categoryId) {
                listRowAdapter.addAll(0, uiState.streamEntities)
            }
            
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
        
        if (uiState.categoryEntities.isNotEmpty()) {
            startEntranceTransition()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainBrowseFragment()
    }
}
