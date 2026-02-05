package com.joshgm3z.triplerocktv.ui.browse.stream

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.ui.browse.StreamPresenter
import com.joshgm3z.triplerocktv.ui.browse.category.BrowseType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StreamCatalogueFragment : VerticalGridSupportFragment() {

    private val viewModel: StreamViewModel by viewModels()
    private val args by navArgs<StreamCatalogueFragmentArgs>()

    private val streamAdapter = ArrayObjectAdapter(StreamPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup the grid presenter (e.g., 5 columns)
        val gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = 5
        }
        setGridPresenter(gridPresenter)

        // You would typically set your adapter here
        adapter = streamAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                streamAdapter.clear()
                streamAdapter.addAll(0, it)
            }
        }
        viewModel.fetchStreams(args.categoryId, BrowseType.VideoOnDemand)
    }
}