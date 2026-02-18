package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
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
            numberOfColumns = 6
        }
        setGridPresenter(gridPresenter)

        // You would typically set your adapter here
        adapter = streamAdapter
        title = args.categoryName

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            val action = StreamCatalogueFragmentDirections.toDetails()
            when (item) {
                is StreamData -> action.apply {
                    streamId = item.streamId
                    streamType = item.streamType
                }

                is SeriesStream -> action.apply {
                    streamId = item.seriesId
                    streamType = StreamType.Series
                }

                else -> return@OnItemViewClickedListener
            }.let {
                findNavController().navigate(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.show()
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                streamAdapter.clear()
                it?.let {
                    progressBarManager.hide()
                    if (it.isEmpty()) {
                        title = "No titles found"
                    } else {
                        streamAdapter.addAll(0, it)
                    }
                }
            }
        }
        viewModel.fetchStreams(args.categoryId, args.streamType)
    }
}