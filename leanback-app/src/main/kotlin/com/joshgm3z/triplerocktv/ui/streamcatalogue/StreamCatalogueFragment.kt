package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueUiState
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueViewModel
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.ui.common.diffCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StreamCatalogueFragment : VerticalGridSupportFragment() {

    private val viewModel: CatalogueViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    lateinit var rowsAdapter: PagingDataAdapter<Any>

    private val args: StreamCatalogueFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRowFragment()
        title = args.categoryName
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun initRowFragment() {
        gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 5
        }
        rowsAdapter = PagingDataAdapter(streamPresenter, diffCallback)
        adapter = rowsAdapter
        onItemViewClickedListener = clickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                when (it) {
                    is CatalogueUiState.VideoOnDemand -> {
                        it.pagingStreams.collectLatest {
                            rowsAdapter.submitData(it as PagingData<Any>)
                        }
                    }

                    is CatalogueUiState.Series -> {
                        it.pagingStreams.collectLatest {
                            rowsAdapter.submitData(it as PagingData<Any>)
                        }
                    }

                    else -> return@collectLatest
                }
            }
        }
    }

    private val clickListener = OnItemViewClickedListener { _, item, _, _ ->
        when (item) {
            is StreamData -> when (item.streamType) {
                StreamType.VideoOnDemand -> StreamCatalogueFragmentDirections.toDetails()
                    .apply {
                        streamId = item.streamId
                        streamType = item.streamType
                    }

                else -> StreamCatalogueFragmentDirections.toPlayback().apply {
                    streamId = item.streamId
                    streamType = item.streamType
                }
            }

            is SeriesStream -> StreamCatalogueFragmentDirections.toDetails().apply {
                streamId = item.seriesId
                streamType = StreamType.Series
            }

            else -> return@OnItemViewClickedListener
        }.let {
            findNavController().navigate(it)
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(
            ScreenName.Catalogue,
            mapOf("catalogue_streamType" to args.streamType.name)
        )
    }
}