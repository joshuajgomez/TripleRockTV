package com.joshgm3z.triplerocktv.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.MediaSyncService
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.selfupdate.ApkInstaller
import com.joshgm3z.triplerocktv.core.selfupdate.FileDownloader
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

    @Inject
    lateinit var fileDownloader: FileDownloader

    @Inject
    lateinit var apkInstaller: ApkInstaller

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        )
    )

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    private val homeAdapter = ArrayObjectAdapter(HomeItemPresenter())

    private val settingsAdapter = ArrayObjectAdapter(SettingsItemPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        headersState = HEADERS_DISABLED
        adapter = rowsAdapter
        onItemViewClickedListener = itemViewClickedListener

        progressBarManager.show()
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_vd_vector)

        MediaSyncService.restart(requireContext())

        rowsAdapter.add(ListRow(homeAdapter))
        rowsAdapter.add(ListRow(settingsAdapter))
    }

    private val itemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
        when (item) {
            is SettingItem -> when (item.title) {
                "Sign out" -> HomeFragmentDirections.toSignOutDialog()
                "Update app" -> HomeFragmentDirections.toAppUpdateDialog()

                else -> HomeFragmentDirections.toSettings()
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
                    homeAdapter.setItems(it, diffCallback)
                    val settings = listOf(
                        SettingItem("Update app", R.drawable.outline_deployed_code_update_24),
                        SettingItem("Settings", R.drawable.ic_settings),
                        SettingItem("Sign out", R.drawable.icon_logout)
                    )
                    settingsAdapter.setItems(settings, diffCallback)
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

private val diffCallback: DiffCallback<Any> = object : DiffCallback<Any>() {
    override fun areItemsTheSame(item1: Any, item2: Any): Boolean {
        return if (item1 is HomeItem && item2 is HomeItem) {
            item1.title == item2.title
        } else if (item1 is SettingItem && item2 is SettingItem) {
            item1.title == item2.title
        } else false
    }

    override fun areContentsTheSame(item1: Any, item2: Any): Boolean {
        return if (item1 is HomeItem && item2 is HomeItem) {
            item1 == item2
        } else if (item1 is SettingItem && item2 is SettingItem) {
            item1 == item2
        } else false
    }
}