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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.ui.browse.category.CategoryPresenter
import com.joshgm3z.triplerocktv.ui.browse.recents.RecentStreamPresenter
import com.joshgm3z.triplerocktv.ui.browse.settings.SettingsItemPresenter
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

data class SettingItem(
    val title: String,
    val iconRes: Int,
)

@AndroidEntryPoint
class MainBrowseFragment : BrowseSupportFragment() {

    private val viewModel: BrowseViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var recentStreamPresenter: RecentStreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var categoryPresenter: CategoryPresenter

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupEventListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    updateRows(uiState)
                }
            }
        }
        prepareEntranceTransition()
    }

    private fun setupUI() {
        brandColor = requireContext().getBackgroundColor()
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_3rocktv_cutout)

        searchAffordanceColor = requireContext().getBackgroundColor()

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            findNavController().navigate(MainBrowseFragmentDirections.toSearch())
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
            is SettingItem -> {
                when (item.title) {
                    "Sign out" -> MainBrowseFragmentDirections.toConfirmSignOutDialog()
                    "All settings" -> MainBrowseFragmentDirections.toSettings()
                    else -> MainBrowseFragmentDirections.toMediaLoading()
                }
            }

            is CategoryData -> MainBrowseFragmentDirections.toStreamCatalogue().apply {
                categoryId = item.categoryId
                categoryName = item.categoryName
                streamType = item.streamType
            }

            is SeriesStream -> MainBrowseFragmentDirections.toDetails().apply {
                streamType = StreamType.Series
                streamId = item.seriesId
            }

            is StreamData -> MainBrowseFragmentDirections.toDetails().apply {
                streamType = item.streamType
                streamId = item.streamId
            }

            else -> return
        }.let { findNavController().navigate(it) }
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

    private fun updateRows(uiState: BrowseUiState) {
        rowsAdapter.clear()
        if (uiState.loading) {
            progressBarManager.show()
            return
        } else progressBarManager.hide()

        addRecentsRow(uiState.recentPlayed)
        addMyListRow(uiState.myList)
        var counter = 1L
        uiState.categoryMap.forEach { (title, categories) ->
            addRow(counter++, title, categories)
        }
        addSettingsRow()
    }

    private fun addRecentsRow(list: List<Any>) {
        if (list.isEmpty()) return
        val header = HeaderItem(0, "Recently played")
        val listRowAdapter = ArrayObjectAdapter(recentStreamPresenter)
        listRowAdapter.addAll(0, list)
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun addMyListRow(list: List<Any>) {
        if (list.isEmpty()) return
        val header = HeaderItem(0, "My list")
        val listRowAdapter = ArrayObjectAdapter(streamPresenter)
        listRowAdapter.addAll(0, list)
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun addRow(
        id: Long,
        header: String,
        list: List<CategoryData>
    ) {
        Logger.debug("id = [${id}], header = [${header}], list = [${list}]")
        if (list.isEmpty()) return
        val header = HeaderItem(id, header)
        val listRowAdapter = ArrayObjectAdapter(categoryPresenter)
        listRowAdapter.addAll(0, list)
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun addSettingsRow() {
        val SETTINGS_ID = -1L
        val header = HeaderItem(SETTINGS_ID, "Settings")
        // We add a ListRow with an empty adapter because we only care about the header click
        val adapter = ArrayObjectAdapter(SettingsItemPresenter())
        adapter.add(SettingItem("Update", R.drawable.icon_download))
        adapter.add(SettingItem("All settings", R.drawable.ic_settings))
        adapter.add(SettingItem("Sign out", R.drawable.icon_logout))
        rowsAdapter.add(ListRow(header, adapter))
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isBlurSettingEnabled) {
            requireActivity().setBackground(null)
        }
        viewModel.onViewResumed()
    }
}