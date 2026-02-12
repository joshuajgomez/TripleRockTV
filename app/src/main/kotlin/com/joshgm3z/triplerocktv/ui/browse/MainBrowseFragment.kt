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
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.category.CategoryPresenter
import com.joshgm3z.triplerocktv.ui.browse.settings.SettingsItemPresenter
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingItem(
    val title: String,
    val iconRes: Int,
)

@AndroidEntryPoint
class MainBrowseFragment : BrowseSupportFragment() {

    private val viewModel: BrowseViewModel by viewModels()

    private lateinit var rowsAdapter: ArrayObjectAdapter

    private lateinit var backgroundManager: BackgroundManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareBackgroundManager()
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

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(requireActivity())
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
            handleBlur(item)
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

            is VodCategory -> MainBrowseFragmentDirections
                .toStreamCatalogue()
                .setCategoryId(item.categoryId)
                .setCategoryName(item.categoryName)
                .setBrowseType(BrowseType.VideoOnDemand)

            is LiveTvCategory -> MainBrowseFragmentDirections
                .toStreamCatalogue()
                .setCategoryId(item.categoryId)
                .setCategoryName(item.categoryName)
                .setBrowseType(BrowseType.LiveTV)


            is SeriesCategory -> MainBrowseFragmentDirections
                .toStreamCatalogue()
                .setCategoryId(item.categoryId)
                .setCategoryName(item.categoryName)
                .setBrowseType(BrowseType.Series)

            is SeriesStream -> MainBrowseFragmentDirections
                .toDetails()
                .setBrowseType(BrowseType.Series)
                .setStreamId(item.seriesId)

            is VodStream -> MainBrowseFragmentDirections
                .toDetails()
                .setBrowseType(BrowseType.VideoOnDemand)
                .setStreamId(item.streamId)

            is LiveTvStream -> MainBrowseFragmentDirections
                .toDetails()
                .setBrowseType(BrowseType.LiveTV)
                .setStreamId(item.streamId)

            else -> return
        }.let { findNavController().navigate(it) }
    }

    private fun handleBlur(item: Any?) = when (item) {
        is VodCategory -> item.firstStreamIcon
        is LiveTvCategory -> item.firstStreamIcon
        is SeriesCategory -> item.firstStreamIcon
        else -> null
    }?.let {
        if (viewModel.isBlurSettingEnabled)
            updateBackgroundWithBlur(requireContext(), it) {
                backgroundManager.setBitmap(it)
            }
    }

    private fun updateRows(uiState: BrowseUiState) {
        rowsAdapter.clear()
        addRecentsRow(uiState.recentPlayed)
        addRow(1, "Video on demand", uiState.vodCategories)
        addRow(2, "Series", uiState.seriesCategories)
        addRow(3, "Live TV", uiState.liveTvCategories)
        addRow(4, "EPG", uiState.epgCategories)
        addSettingsRow()
    }

    private fun addRecentsRow(list: List<Any>) {
        if (list.isEmpty()) return
        val header = HeaderItem(0, "Recently played")
        val listRowAdapter = ArrayObjectAdapter(StreamPresenter(isShortCard = true))
        listRowAdapter.addAll(0, list)
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun addRow(
        id: Long,
        header: String,
        list: List<Any>
    ) {
        if (list.isEmpty()) return
        val header = HeaderItem(id, header)
        val listRowAdapter = ArrayObjectAdapter(CategoryPresenter())
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
            backgroundManager.drawable = null
        }
    }
}