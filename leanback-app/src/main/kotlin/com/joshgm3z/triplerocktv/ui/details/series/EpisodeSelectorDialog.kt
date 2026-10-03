package com.joshgm3z.triplerocktv.ui.details.series

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.viewmodel.EpisodeSelectorViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorUiState
import com.joshgm3z.triplerocktv.databinding.DialogEpisodeSelectorBinding
import com.joshgm3z.triplerocktv.ui.common.diffCallback
import com.joshgm3z.triplerocktv.util.GlideUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeSelectorDialog : DialogFragment(R.layout.dialog_episode_selector) {

    private val viewModel: EpisodeSelectorViewModel by viewModels()

    private val args by navArgs<EpisodeSelectorDialogArgs>()

    private lateinit var binding: DialogEpisodeSelectorBinding

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    private lateinit var episodeAdapter: ArrayObjectAdapter

    private lateinit var seasonArrayObjectAdapter: ArrayObjectAdapter

    private val seasonPresenter = SeasonPresenter()

    private lateinit var seasonsGridFragment: VerticalGridSupportFragment

    private lateinit var episodesGridFragment: VerticalGridSupportFragment

    private val seasons: List<Season>
        get() = viewModel.uiState.value.seasons

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_height)
        )
    }

    private fun initEpisodeAdapter() {
        val gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 1
        }
        episodeAdapter = ArrayObjectAdapter(EpisodePresenter(glideUtil))

        episodesGridFragment = childFragmentManager.findFragmentById(R.id.fcv_episodes_list)
                as VerticalGridSupportFragment
        episodesGridFragment.setGridPresenter(gridPresenter)
        episodesGridFragment.adapter = episodeAdapter
        episodesGridFragment.onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                val episode = item as? Episode
                if (episode != null) {
                    navigateToPlayback(episode.id)
                }
            }
    }

    private fun initSeasonAdapter() {
        val gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 1
        }
        seasonArrayObjectAdapter = ArrayObjectAdapter(seasonPresenter)

        seasonsGridFragment = childFragmentManager.findFragmentById(R.id.fcv_season_list)
                as VerticalGridSupportFragment
        seasonsGridFragment.setGridPresenter(gridPresenter)
        seasonsGridFragment.adapter = seasonArrayObjectAdapter
        seasonsGridFragment.onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            binding.fcvEpisodesList.requestFocus()
        }
        seasonsGridFragment.setOnItemViewSelectedListener { _, item, _, _ ->
            val season = item as? Season
            viewModel.selectedSeasonNumber.value = season?.number
        }
        lifecycleScope.launch {
            viewModel.selectedSeasonNumber.debounce(50).collectLatest {
                seasonPresenter.selectedSeasonNumber = it
                binding.fcvSeasonList.post {
                    seasonArrayObjectAdapter.notifyItemRangeChanged(0, seasons.size)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogEpisodeSelectorBinding.bind(view)
        initEpisodeAdapter()
        initSeasonAdapter()

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                updateUI(it)
            }
        }
        lifecycleScope.launch {
            viewModel.episodesFlow.collectLatest {
                episodeAdapter.setItems(it, diffCallback)
            }
        }
    }

    private fun updateUI(uiState: SeriesSelectorUiState) {
        seasonPresenter.selectedSeasonNumber = uiState.selectedSeasonNumber
        seasonArrayObjectAdapter.setItems(uiState.seasons, diffCallback)
        uiState.selectedSeasonNumber?.let {
            val seasonIndex = uiState.seasons.indexOfFirst {
                it.number == uiState.selectedSeasonNumber
            }
            seasonsGridFragment.setSelectedPosition(seasonIndex)
        }
        uiState.selectedEpisodeIndex?.let {
            episodesGridFragment.setSelectedPosition(it)
        }
        binding.fcvEpisodesList.requestFocus()
    }

    fun navigateToPlayback(episodeId: Int) {
        EpisodeSelectorDialogDirections.toPlayback().apply {
            this.seriesId = args.seriesId
            this.streamId = episodeId
            this.streamType = StreamType.Series
            findNavController().navigate(this)
        }
    }
}
