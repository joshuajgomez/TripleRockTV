package com.joshgm3z.triplerocktv.ui.browse

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.ui.browse.category.CategoryPresenter
import com.joshgm3z.triplerocktv.ui.browse.recents.RecentStreamPresenter
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.util.DimMode
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.Logger
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

@AndroidEntryPoint
class BrowseFragment : BrowseSupportFragment() {

    private val viewModel: BrowseViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var recentStreamPresenter: RecentStreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var categoryPresenter: CategoryPresenter

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = rowsAdapter
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                Logger.debug("uiState = $it")
                if (it !is BrowseUiState.Loading) progressBarManager.hide()
                when (it) {
                    is BrowseUiState.Loading -> progressBarManager.show()
                    is BrowseUiState.VideoOnDemandState -> showStreamDataState(it)
                    is BrowseUiState.SeriesStreamState -> showSeriesStreamState(it)
                    is BrowseUiState.Error -> BrowseFragmentDirections.toError(it.message)
                    else -> throw Exception("Invalid state")
                }
            }
        }
        setupEventListeners()
    }

    private fun setupUI() {
        brandColor = requireContext().getBackgroundColor()
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_3rocktv_cutout)

        searchAffordanceColor = requireContext().getBackgroundColor()
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            findNavController().navigate(BrowseFragmentDirections.toSearch())
        }
        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, row ->
            when (item) {
                is CategoryData -> handleBlur(item.firstStreamIcon)
                is StreamData -> handleBlur(item.streamIcon)
            }
        }

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, row ->
            // Handle item click if needed
            handleClick(item)
        }
    }

    private fun handleClick(item: Any?) {
        when (item) {

            is CategoryData -> BrowseFragmentDirections.toStreamCatalogue().apply {
                categoryId = item.categoryId
                categoryName = item.categoryName
                streamType = item.streamType
            }

            is SeriesStream -> BrowseFragmentDirections.toSeriesDetails().apply {
                seriesId = item.seriesId
            }

            is StreamData -> BrowseFragmentDirections.toDetails().apply {
                streamType = item.streamType
                streamId = item.streamId
            }

            else -> return
        }.let { findNavController().navigate(it) }
    }

    private fun showStreamDataState(uiState: BrowseUiState.VideoOnDemandState) {
        rowsAdapter.clear()

        if (uiState.recentPlayed.isNotEmpty()) {
            val header = HeaderItem(0, "Recently played")
            val listRowAdapter = ArrayObjectAdapter(recentStreamPresenter)
            listRowAdapter.addAll(0, uiState.recentPlayed)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        if (uiState.myList.isNotEmpty()) {
            val header = HeaderItem(0, "My list")
            val listRowAdapter = ArrayObjectAdapter(streamPresenter)
            listRowAdapter.addAll(0, uiState.myList)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        fun addRow(
            id: Long,
            header: String,
            list: List<CategoryData>
        ) {
            if (list.isEmpty()) return
            val header = HeaderItem(id, header)
            val listRowAdapter = ArrayObjectAdapter(categoryPresenter)
            listRowAdapter.addAll(0, list)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        var counter = 1L
        uiState.categoryMap.forEach { (title, categories) ->
            addRow(counter++, title, categories)
        }
    }

    private fun showSeriesStreamState(uiState: BrowseUiState.SeriesStreamState) {
        rowsAdapter.clear()

        if (uiState.myList.isNotEmpty()) {
            val header = HeaderItem(0, "My list")
            val listRowAdapter = ArrayObjectAdapter(streamPresenter)
            listRowAdapter.addAll(0, uiState.myList)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        fun addRow(
            id: Long,
            header: String,
            list: List<CategoryData>
        ) {
            if (list.isEmpty()) return
            val header = HeaderItem(id, header)
            val listRowAdapter = ArrayObjectAdapter(categoryPresenter)
            listRowAdapter.addAll(0, list)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
        addRow(0L, "Series", uiState.seriesCategories)
    }

    private fun handleBlur(thumbnailUrl: String?) {
        thumbnailUrl?.let {
            if (viewModel.isBlurSettingEnabled)
                glideUtil.getBitmap(uri = it, blur = true, dimMode = DimMode.Darker) { bitmap ->
                    if (!isVisible) return@getBitmap
                    requireActivity().setBackground(bitmap)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }
}