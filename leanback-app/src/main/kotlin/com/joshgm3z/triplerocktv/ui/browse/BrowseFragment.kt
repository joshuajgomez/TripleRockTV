package com.joshgm3z.triplerocktv.ui.browse

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import com.joshgm3z.triplerocktv.util.DimMode
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseUiState
import com.joshgm3z.triplerocktv.core.viewmodel.BrowseViewModel
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BrowseFragment : BrowseSupportFragment() {

    private val viewModel: BrowseViewModel by viewModels()

    private val args by navArgs<BrowseFragmentArgs>()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var recentStreamPresenter: RecentStreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var categoryPresenter: CategoryPresenter

    private val episodeToSeriesMap = mutableMapOf<Int, Int>()

    private val rowsAdapter = ArrayObjectAdapter(
        ListRowPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = rowsAdapter
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseLogger.logScreenView(ScreenName.Browse, mapOf("browse_streamType" to args.streamType.name))

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                Logger.debug("uiState = $it")
                if (it !is BrowseUiState.Loading) progressBarManager.hide()
                when (it) {
                    is BrowseUiState.Loading -> progressBarManager.show()
                    is BrowseUiState.VideoOnDemandState -> showStreamDataState(it)
                    is BrowseUiState.SeriesStreamState -> showSeriesStreamState(it)
                    is BrowseUiState.Error -> BrowseFragmentDirections.toError(it.message)
                }
            }
        }
        setupEventListeners()
    }

    private fun setupUI() {
        brandColor = requireContext().getBackgroundColor()
        val icon = when (args.streamType) {
            StreamType.VideoOnDemand -> R.drawable.movie_avd
            StreamType.Series -> R.drawable.series_avd
            StreamType.LiveTV -> R.drawable.livetv_avd
        }
        badgeDrawable = ContextCompat.getDrawable(requireContext(), icon)

        searchAffordanceColor = requireContext().getBackgroundColor()
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            findNavController().navigate(BrowseFragmentDirections.toSearch())
        }
        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, _ ->
            when (item) {
                is CategoryData -> handleBlur(item.firstStreamIcon)
                is StreamData -> handleBlur(item.streamIcon)
                is SeriesStream -> handleBlur(item.coverImageUrl)
                is Episode -> handleBlur(item.episodeInfo?.movie_image)
            }
        }

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, row ->
            // Handle item click if needed
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

                is Episode -> BrowseFragmentDirections.toSeriesDetails().apply {
                    seriesId = episodeToSeriesMap[item.id]!!
                }

                else -> return@OnItemViewClickedListener
            }.let { findNavController().navigate(it) }
        }
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

        if (uiState.recentPlayedEpisodes.isNotEmpty()) {
            val episodes = uiState.recentPlayedEpisodes.map {
                episodeToSeriesMap[it.lastPlayedEpisodeId] = it.seriesId
                it.seasons?.findEpisode(it.lastPlayedEpisodeId)
            }
            val header = HeaderItem(0, "Recently played")
            val listRowAdapter = ArrayObjectAdapter(recentStreamPresenter)
            listRowAdapter.addAll(0, episodes)
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
        addRow(0L, "All series", uiState.seriesCategories)
    }

    private fun List<Season>.findEpisode(episodeId: Int): Episode {
        forEach {
            it.episodes.forEach { episode ->
                if (episode.id == episodeId) return episode
            }
        }
        throw Exception("EpisodeId $episodeId not found")
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