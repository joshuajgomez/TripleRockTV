package com.joshgm3z.triplerocktv.ui.details.series

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.room.series.Season
import com.joshgm3z.triplerocktv.ui.details.EpisodePresenter
import com.joshgm3z.triplerocktv.ui.details.SeriesDetailsFragmentArgs
import com.joshgm3z.triplerocktv.util.DimMode
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SeriesSelectorFragment : RowsSupportFragment() {

    private val SEASON_INFO_ROW = 0
    private val SEASON_ROW = 1
    private val EPISODE_ROW = 2

    private val viewModel: SeriesSelectorViewModel by viewModels()

    private val args by navArgs<SeriesDetailsFragmentArgs>()

    @Inject
    lateinit var glideUtil: GlideUtil

    private val seasonPresenter = SeasonPresenter()

    private val seasonRowAdapter = ArrayObjectAdapter(seasonPresenter)

    private lateinit var seasonInfoAdapter: ArrayObjectAdapter

    private val rowsAdapter = ArrayObjectAdapter(ClassPresenterSelector().apply {
        addClassPresenter(
            ListRow::class.java, ListRowPresenter(
                FocusHighlight.ZOOM_FACTOR_SMALL,
                false
            ).apply {
                shadowEnabled = false
                selectEffectEnabled = false
            }
        )
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = rowsAdapter

        seasonInfoAdapter = ArrayObjectAdapter(SeasonInfoPresenter(glideUtil))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verticalGridView.setPadding(0, 80, 0, 0)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                updateUI(it)
            }
        }
        registerListener()
    }

    private fun registerListener() {
        onItemViewSelectedListener =
            OnItemViewSelectedListener { _, item, _, _ ->
                if (selectedPosition == SEASON_INFO_ROW) {
                    selectedPosition = SEASON_ROW
                    return@OnItemViewSelectedListener
                }
                if (item is Season) {
                    viewModel.onSeasonSelected(item.number)
                }
            }

        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                when (item) {
                    is Season -> setSelectedPosition(
                        EPISODE_ROW,
                        true,
                        ListRowPresenter.SelectItemViewHolderTask(0)
                    )

                    is Episode -> SeriesSelectorFragmentDirections.toPlayback().apply {
                        this.seriesId = args.seriesId
                        this.streamId = item.id
                        this.streamType = StreamType.Series
                        findNavController().navigate(this)
                    }
                }
            }
    }

    private fun updateUI(state: SeriesSelectorUiState) {
        if (state.seasons.isEmpty()) return

        val selectedSeason = state.seasons.filter { it.number == state.selectedSeasonNumber }
        view?.post {
            seasonInfoAdapter.setItems(selectedSeason, null)
        }

        val seasonInfoRow = ListRow(seasonInfoAdapter)
        if (rowsAdapter.size() > SEASON_INFO_ROW) {
            rowsAdapter.replace(SEASON_INFO_ROW, seasonInfoRow)
        } else {
            rowsAdapter.add(SEASON_INFO_ROW, seasonInfoRow)
        }

        if (rowsAdapter.size() <= SEASON_ROW) {
            seasonRowAdapter.setItems(state.seasons, null)
            rowsAdapter.add(ListRow(seasonRowAdapter))
        }

        if (rowsAdapter.size() <= EPISODE_ROW) {
            val episodeRowAdapter = ArrayObjectAdapter(EpisodePresenter(glideUtil))
            rowsAdapter.add(EPISODE_ROW, ListRow(episodeRowAdapter))
        }

        seasonPresenter.highlightSeasonNumber = state.selectedSeasonNumber
        seasonRowAdapter.notifyArrayItemRangeChanged(0, seasonRowAdapter.size())

        val episodeRow = rowsAdapter.get(EPISODE_ROW) as? ListRow
        val episodeAdapter = episodeRow?.adapter as? ArrayObjectAdapter

        if (episodeAdapter != null && state.episodes.isNotEmpty()) {
            view?.post {
                episodeAdapter.setItems(state.episodes, null)

                state.selectedEpisodeIndex?.let {
                    setSelectedPosition(
                        EPISODE_ROW, true,
                        ListRowPresenter.SelectItemViewHolderTask(it)
                    )
                }
            }
        }
    }
}