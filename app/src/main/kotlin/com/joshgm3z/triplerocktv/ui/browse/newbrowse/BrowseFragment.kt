package com.joshgm3z.triplerocktv.ui.browse.newbrowse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.repository.room.CategoryData
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

@AndroidEntryPoint
class BrowseFragment : BrowseSupportFragment() {

    private val viewModel: NewBrowseViewModel by viewModels()

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
        brandColor = requireContext().getBackgroundColor()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                Logger.debug("uiState = $it")
                if (it !is MainBrowseUiState.Loading) progressBarManager.hide()
                when (it) {
                    is MainBrowseUiState.Loading -> progressBarManager.show()
                    is MainBrowseUiState.VideoOnDemandState -> showStreamDataState(it)
                    is MainBrowseUiState.Error -> BrowseFragmentDirections.toError(it.message)
                    else -> throw Exception("Invalid state")
                }
            }
        }
    }

    private fun showStreamDataState(uiState: MainBrowseUiState.VideoOnDemandState) {
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

    private fun handleBlur(thumbnailUrl: String?) {
        thumbnailUrl?.let {
            if (viewModel.isBlurSettingEnabled)
                glideUtil.getBitmap(uri = it, blur = true, dimMode = DimMode.Darker) { bitmap ->
                    if (!isVisible) return@getBitmap
                    requireActivity().setBackground(bitmap)
                }
        }
    }
}