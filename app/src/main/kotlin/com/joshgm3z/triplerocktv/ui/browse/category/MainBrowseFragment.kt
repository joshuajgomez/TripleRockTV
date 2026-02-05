package com.joshgm3z.triplerocktv.ui.browse.category

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
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.ui.login.LoginViewModel
import com.joshgm3z.triplerocktv.ui.browse.settings.SettingsItemPresenter
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
    private val loginViewModel: LoginViewModel by viewModels()
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
        brandColor = ContextCompat.getColor(requireContext(), R.color.black)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = "3Rock TV"
        // Initialize BackgroundManager
        val backgroundManager = BackgroundManager.getInstance(requireActivity())
        if (!backgroundManager.isAttached) {
            backgroundManager.attach(requireActivity().window)
        }
        // Set a solid background color for the entire fragment
        backgroundManager.color = ContextCompat.getColor(requireContext(), R.color.black)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.black)

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            // Navigate to your search fragment/activity
            findNavController().navigate(MainBrowseFragmentDirections.actionBrowseToSearch())
        }
        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, row ->
            // Handle item selection if needed
        }

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, row ->
            // Handle item click if needed
            when (item) {
                is SettingItem -> {
                    when (item.title) {
                        "Sign out" -> loginViewModel.onLogoutClick {
                            val action = MainBrowseFragmentDirections.actionBrowseToLogin()
                            findNavController().navigate(action)
                        }

                        else -> findNavController().navigate(
                            MainBrowseFragmentDirections.actionBrowseToMediaLoading()
                        )
                    }
                }

                is VodCategory -> findNavController().navigate(
                    MainBrowseFragmentDirections
                        .actionBrowseToStreamCatalogue()
                        .setCategoryId(item.categoryId)
                        .setCategoryName(item.categoryName)
                        .setBrowseType(BrowseType.VideoOnDemand)
                )

                is LiveTvCategory -> findNavController().navigate(
                    MainBrowseFragmentDirections
                        .actionBrowseToStreamCatalogue()
                        .setCategoryId(item.categoryId)
                        .setCategoryName(item.categoryName)
                        .setBrowseType(BrowseType.LiveTV)
                )

                is SeriesCategory -> findNavController().navigate(
                    MainBrowseFragmentDirections
                        .actionBrowseToStreamCatalogue()
                        .setCategoryId(item.categoryId)
                        .setCategoryName(item.categoryName)
                        .setBrowseType(BrowseType.Series)
                )

            }
        }
    }

    private fun updateRows(uiState: BrowseUiState) {
        rowsAdapter.clear()
        addRow(0, "Video on demand", uiState.vodCategories)
        addRow(1, "Series", uiState.seriesCategories)
        addRow(2, "Live TV", uiState.liveTvCategories)
        addRow(3, "EPG", uiState.epgCategories)
        addSettingsRow()
    }

    private fun addRow(
        id: Long,
        header: String,
        categories: List<Any>
    ) {
        val header = HeaderItem(id, header)
        val listRowAdapter = ArrayObjectAdapter(CategoryPresenter())
        listRowAdapter.addAll(0, categories)
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun addSettingsRow() {
        val SETTINGS_ID = -1L
        val header = HeaderItem(SETTINGS_ID, "Settings")
        // We add a ListRow with an empty adapter because we only care about the header click
        val adapter = ArrayObjectAdapter(SettingsItemPresenter())
        adapter.add(SettingItem("Update", R.drawable.icon_download))
        adapter.add(SettingItem("Sign out", R.drawable.icon_logout))
        rowsAdapter.add(ListRow(header, adapter))
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainBrowseFragment()
    }
}