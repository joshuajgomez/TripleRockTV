package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.os.Bundle
import android.view.View
import android.widget.ZoomButton
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
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
import javax.inject.Inject

@AndroidEntryPoint
class StreamCatalogueFragment : VerticalGridSupportFragment() {

    private val viewModel: StreamViewModel by viewModels()
    private val args by navArgs<StreamCatalogueFragmentArgs>()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    private lateinit var streamAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        streamAdapter = ArrayObjectAdapter(streamPresenter)
        // Setup the grid presenter (e.g., 5 columns)
        val gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_LARGE,
            false
        ).apply {
            numberOfColumns = 6
        }
        setGridPresenter(gridPresenter)

        // You would typically set your adapter here
        adapter = streamAdapter
        title = args.categoryName

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is StreamData -> StreamCatalogueFragmentDirections.toDetails().apply {
                    streamId = item.streamId
                    streamType = item.streamType
                }

                is SeriesStream -> StreamCatalogueFragmentDirections.toSeriesDetails().apply {
                    seriesId = item.seriesId
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
                it?.let {
                    progressBarManager.hide()
                    streamAdapter.setItems(it, null)
                    if (it.isEmpty()) {
                        title = "No titles found"
                    }
                }
            }
        }
        viewModel.fetchStreams(args.categoryId, args.streamType)
    }
}