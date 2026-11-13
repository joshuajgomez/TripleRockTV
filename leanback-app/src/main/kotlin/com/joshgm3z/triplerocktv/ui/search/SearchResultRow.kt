package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.viewmodel.SearchUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SearchViewModel
import com.joshgm3z.triplerocktv.ui.browse.CategoryPresenter
import com.joshgm3z.triplerocktv.ui.common.diffCallback
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.util.fragmentScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultRow : RowsSupportFragment() {

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var categoryPresenter: CategoryPresenter

    private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        )
    )

    private lateinit var streamRowAdapter: ArrayObjectAdapter

    private lateinit var categoryRowAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRows()
        listenToState()
    }

    private fun initRows() {
        streamRowAdapter = ArrayObjectAdapter(streamPresenter)
        categoryRowAdapter = ArrayObjectAdapter(categoryPresenter)

        adapter = rowsAdapter
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            onSearchResultClick(item)
        }
    }

    private fun listenToState() {
        fragmentScope {
            viewModel.uiState.collectLatest {
                when (it) {
                    is SearchUiState.Initial -> setRow("Recently added", it.initialStreams)
                    is SearchUiState.Result -> setResultsRow(it.streams, it.categories)
                    is SearchUiState.Loading -> setRow("Searching")
                    is SearchUiState.NoResult -> setRow("No results found")
                    else -> rowsAdapter.clear()
                }
            }
        }
    }

    private fun setResultsRow(
        streams: List<Any>,
        categories: List<CategoryData>
    ) {
        rowsAdapter.clear()

        val header = HeaderItem(0, "Streams")
        if (streams.isNotEmpty())
            rowsAdapter.add(ListRow(header, streamRowAdapter))
        streamRowAdapter.setItems(streams, diffCallback)

        val categoryHeader = HeaderItem(1, "Categories")
        if (categories.isNotEmpty())
            rowsAdapter.add(ListRow(categoryHeader, categoryRowAdapter))
        categoryRowAdapter.setItems(categories, diffCallback)
    }

    private fun setRow(
        title: String,
        streams: List<Any> = emptyList(),
    ) {
        rowsAdapter.clear()

        val header = HeaderItem(0, title)
        rowsAdapter.add(ListRow(header, streamRowAdapter))
        streamRowAdapter.setItems(streams, diffCallback)
    }

    private fun onSearchResultClick(it: Any) {
        val text: String
        when (it) {
            is StreamData -> when (it.streamType) {
                StreamType.VideoOnDemand -> SearchFragmentDirections.toDetails().apply {
                    text = it.name
                    streamId = it.streamId
                    streamType = it.streamType
                }

                else -> SearchFragmentDirections.toLiveTvCatalogue().apply {
                    text = it.name
                    categoryId = it.categoryId
                    selectedStreamId = it.streamId
                }
            }

            is SeriesStream -> SearchFragmentDirections.toDetails().apply {
                text = it.name
                streamId = it.seriesId
                streamType = StreamType.Series
            }

            is CategoryData -> when (it.streamType) {
                StreamType.LiveTV -> SearchFragmentDirections.toLiveTvCatalogue().apply {
                    text = it.categoryName
                    categoryId = it.categoryId
                }

                else -> SearchFragmentDirections.toStreamsCatalogue().apply {
                    text = it.categoryName
                    categoryId = it.categoryId
                    streamType = it.streamType
                }
            }

            else -> return
        }.let { action ->
            findNavController().navigate(action)
        }
        viewModel.saveSearchHint(text)
    }
}
