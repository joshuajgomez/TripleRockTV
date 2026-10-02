package com.joshgm3z.triplerocktv.ui.details.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.viewmodel.EpisodeSelectorViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorUiState
import com.joshgm3z.triplerocktv.databinding.DialogEpisodeSelectorBinding
import com.joshgm3z.triplerocktv.ui.common.diffCallback
import com.joshgm3z.triplerocktv.util.GlideUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeSelectorDialog : DialogFragment(R.layout.dialog_episode_selector) {

    private val viewModel: EpisodeSelectorViewModel by viewModels()
    private lateinit var binding: DialogEpisodeSelectorBinding

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    private val args by navArgs<EpisodeSelectorDialogArgs>()

    private val seasonAdapter = SeasonAdapter {
        viewModel.onSeasonSelected(it.number)
    }

    private lateinit var episodeAdapter: ArrayObjectAdapter

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_height)
        )
    }

    private fun initEpisodeAdapter() {
        val gridFragment = VerticalGridSupportFragment()
        val gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 1
        }
        gridFragment.setGridPresenter(gridPresenter)

        episodeAdapter = ArrayObjectAdapter(EpisodePresenter(glideUtil))
        gridFragment.adapter = episodeAdapter
        gridFragment.onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            val episode = item as? Episode
            if (episode != null) {
                navigateToPlayback(episode.id)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(binding.episodeGridContainer.id, gridFragment)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogEpisodeSelectorBinding.bind(view)
        initEpisodeAdapter()

        binding.rvSeasonChips.adapter = seasonAdapter

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                updateUI(it)
            }
        }
    }

    private fun updateUI(uiState: SeriesSelectorUiState) {
        seasonAdapter.selectedSeasonNumber = uiState.selectedSeasonNumber
        val seasonIndex = uiState.seasons.indexOfFirst { it.number == uiState.selectedSeasonNumber }
        binding.rvSeasonChips.scrollToPosition(seasonIndex)
        if (seasonAdapter.seasons.isEmpty()) seasonAdapter.seasons = uiState.seasons

        episodeAdapter.clear()
        episodeAdapter.setItems(uiState.episodes, diffCallback)
        uiState.selectedEpisodeIndex?.let {
            val gridView = view?.findViewById<VerticalGridView>(
                androidx.leanback.R.id.browse_grid
            )
            gridView?.selectedPosition = it
        }
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
