package com.joshgm3z.triplerocktv.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.core.viewmodel.HomeItem
import com.joshgm3z.triplerocktv.core.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment() {
    private val viewModel: HomeViewModel by viewModels()

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        )
    )

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        headersState = HEADERS_DISABLED
        adapter = rowsAdapter
        onItemViewClickedListener = itemViewClickedListener

        progressBarManager.show()
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_vd_vector)
    }

    private val itemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
        when (item) {
            is SettingItem -> when (item.title) {
                "Sign out" -> HomeFragmentDirections.toConfirmSignOutDialog()
                "Settings" -> HomeFragmentDirections.toSettings()
                else -> HomeFragmentDirections.toUpdater()
            }

            is HomeItem -> when (item.title) {
                "Video on demand" -> HomeFragmentDirections.toBrowse(StreamType.VideoOnDemand)
                "Series" -> HomeFragmentDirections.toBrowse(StreamType.Series)
                "Live TV" -> HomeFragmentDirections.toBrowse(StreamType.LiveTV)
                else -> return@OnItemViewClickedListener
            }

            else -> return@OnItemViewClickedListener
        }.let {
            findNavController().navigate(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.homeListState.collectLatest {
                    if (it.isEmpty()) return@collectLatest

                    progressBarManager.hide()
                    rowsAdapter.clear()

                    val homeAdapter = ArrayObjectAdapter(HomeItemPresenter())
                    homeAdapter.addAll(0, it)
                    rowsAdapter.add(ListRow(homeAdapter))

                    val settingsAdapter = ArrayObjectAdapter(SettingsItemPresenter())
                    settingsAdapter.add(
                        SettingItem(
                            "Update",
                            R.drawable.icon_download,
                            viewModel.lastUpdatedTime ?: ""
                        )
                    )
                    settingsAdapter.add(SettingItem("Settings", R.drawable.ic_settings))
                    settingsAdapter.add(SettingItem("Sign out", R.drawable.icon_logout))
                    rowsAdapter.add(ListRow(settingsAdapter))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(ScreenName.Home)
        viewModel.fetchHomeData()
    }
}