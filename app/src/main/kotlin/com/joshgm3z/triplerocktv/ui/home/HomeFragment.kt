package com.joshgm3z.triplerocktv.ui.home

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.VerticalGridPresenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.ui.browse.colorFilter
import com.joshgm3z.triplerocktv.util.GlideUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : VerticalGridSupportFragment() {

    private lateinit var homeAdapter: ArrayObjectAdapter

    @Inject
    lateinit var glideUtil: GlideUtil

    private lateinit var backgroundManager: BackgroundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeAdapter = ArrayObjectAdapter(HomeCardPresenter())
        val gridPresenter = VerticalGridPresenter().apply { numberOfColumns = 4 }
        setGridPresenter(gridPresenter)

        adapter = homeAdapter

        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_3rocktv_cutout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeAdapter.add(HomeCard("Video on demand", StreamType.VideoOnDemand.icon()))
        homeAdapter.add(HomeCard("Live TV", StreamType.LiveTV.icon()))
        homeAdapter.add(HomeCard("Series", StreamType.Series.icon()))
        homeAdapter.add(HomeCard("EPG", R.drawable.ic_epg))
        homeAdapter.add(HomeCard("Update", R.drawable.icon_download))
        homeAdapter.add(HomeCard("Settings", R.drawable.ic_settings))

        backgroundManager = BackgroundManager.getInstance(requireActivity())
        glideUtil.getBitmap(uri = "", blur = true) { bitmap ->
            val canvas = Canvas(bitmap)
            val paint = Paint()
            // Set color to black with 50% alpha (128)
            paint.colorFilter = colorFilter
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            backgroundManager.setBitmap(bitmap)
        }
    }
}

data class HomeCard(
    val title: String,
    val iconRes: Int
)

private fun StreamType.icon(): Int = when (this) {
    StreamType.VideoOnDemand -> R.drawable.ic_movie
    StreamType.LiveTV -> R.drawable.ic_live_tv
    StreamType.Series -> R.drawable.ic_series
}