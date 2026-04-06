package com.joshgm3z.triplerocktv.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.util.DimMode
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment() {

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        )
    )

    @Inject
    lateinit var glideUtil: GlideUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        headersState = HEADERS_DISABLED
        adapter = rowsAdapter
        onItemViewClickedListener = itemViewClickedListener

        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_vd_vector)
    }

    private val itemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
        when (item) {
            is SettingItem -> when (item.title) {
                "Sign out" -> HomeFragmentDirections.toConfirmSignOutDialog()
                "Settings" -> HomeFragmentDirections.toSettings()
                else -> HomeFragmentDirections.toMediaLoading()
            }

            is HomeItem -> when (item.title) {
                "Video on demand" -> HomeFragmentDirections.toBrowse(StreamType.VideoOnDemand)
                "Series" -> HomeFragmentDirections.toBrowse(StreamType.Series)
                else -> return@OnItemViewClickedListener
            }

            else -> return@OnItemViewClickedListener
        }.let {
            findNavController().navigate(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rowsAdapter.clear()

        val homeAdapter = ArrayObjectAdapter(HomeItemPresenter())
        homeAdapter.add(HomeItem("Video on demand", R.drawable.movie_avd))
        homeAdapter.add(HomeItem("Series", R.drawable.series_avd))
        homeAdapter.add(HomeItem("Live TV", R.drawable.livetv_avd))
        rowsAdapter.add(ListRow(homeAdapter))

        val settingsAdapter = ArrayObjectAdapter(SettingsItemPresenter())
        settingsAdapter.add(SettingItem("Update", R.drawable.icon_download))
        settingsAdapter.add(SettingItem("Settings", R.drawable.ic_settings))
        settingsAdapter.add(SettingItem("Sign out", R.drawable.icon_logout))
        rowsAdapter.add(ListRow(settingsAdapter))


        glideUtil.getBitmap(
            uri = "http://riseiptv.xyz:8080/images/f84cfc4a649186588214ccff8a8c335a.jpg",
            blur = true,
            dimMode = DimMode.Dark
        ) {
            if (!isVisible) return@getBitmap
            requireActivity().setBackground(it)
        }
    }
}