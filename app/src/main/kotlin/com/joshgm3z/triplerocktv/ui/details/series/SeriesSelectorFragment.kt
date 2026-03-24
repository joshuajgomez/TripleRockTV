package com.joshgm3z.triplerocktv.ui.details.series

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.HeaderItem
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeriesSelectorFragment : RowsSupportFragment() {

    private val viewModel: SeriesSelectorViewModel by viewModels()

    private val args by navArgs<SeriesDetailsFragmentArgs>()

    private val SEASON_ROW = 0
    private val EPISODE_ROW = 1

    private val rowsAdapter = ArrayObjectAdapter(ClassPresenterSelector().apply {
        addClassPresenter(ListRow::class.java, ListRowPresenter())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = rowsAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                if (item is Season) {
                    viewModel.onSeasonSelected(item.number)
                }
            }

        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                when (item) {
                    is Season -> setSelectedPosition(EPISODE_ROW, true)
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

        if (rowsAdapter.size() == 0) {
            val seasonRowAdapter = ArrayObjectAdapter(SeasonPresenter())
            seasonRowAdapter.addAll(0, state.seasons)
            rowsAdapter.add(
                ListRow(
                    HeaderItem(
                        SEASON_ROW.toLong(),
                        "Select a season"
                    ), seasonRowAdapter
                )
            )

            // Initial empty episodes row
            val episodeRowAdapter = ArrayObjectAdapter(EpisodePresenter())
            rowsAdapter.add(ListRow(episodeRowAdapter))
        }

        val episodeRow = rowsAdapter.get(EPISODE_ROW) as? ListRow
        val episodeAdapter = episodeRow?.adapter as? ArrayObjectAdapter

        // Check if data actually changed to avoid infinite loops or jitter
        if (episodeAdapter != null && state.episodes.isNotEmpty()) {
            // Wrap in post to avoid the "RecyclerView is computing layout" crash
            view?.post {
                episodeAdapter.setItems(state.episodes, null)
            }
        }
    }
}