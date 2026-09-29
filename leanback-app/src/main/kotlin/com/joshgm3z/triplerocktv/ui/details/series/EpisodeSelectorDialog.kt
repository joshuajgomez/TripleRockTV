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

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_height)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding = DialogEpisodeSelectorBinding.bind(view)

        binding.rvSeasonChips.adapter = seasonAdapter

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                updateUI(it)
            }
        }
    }

    private fun updateUI(uiState: SeriesSelectorUiState) {
        seasonAdapter.selectedSeasonNumber = uiState.selectedSeasonNumber
        if (seasonAdapter.seasons.isEmpty()) seasonAdapter.seasons = uiState.seasons
    }

    // Helper method called by the child fragment to trigger playback navigation
    fun navigateToPlayback(episodeId: Int) {
        EpisodeSelectorDialogDirections.toPlayback().apply {
            this.seriesId = args.seriesId
            this.streamId = episodeId
            this.streamType = StreamType.Series
            findNavController().navigate(this)
        }
    }
}

@AndroidEntryPoint
class EpisodeGridFragment : VerticalGridSupportFragment() {

    // 1. Share the ViewModel with the parent dialog fragment
    private val viewModel: EpisodeSelectorViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var glideUtil: GlideUtil

    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Define the grid presenter with 1 column
        val gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 1
        }
        setGridPresenter(gridPresenter)

        // 3. Set up the adapter with your custom EpisodePresenter
        mAdapter = ArrayObjectAdapter(EpisodePresenter(glideUtil))
        adapter = mAdapter

        setupEventListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 4. Collect the data flow from the shared view model
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                mAdapter.clear()
                mAdapter.setItems(uiState.episodes, diffCallback)

                uiState.selectedEpisodeIndex?.let {
                    setSelectedPosition(it)
                }
            }
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            // Replace 'Any' with your explicit Episode model class name
            val episode = item as? Episode
            if (episode != null) {
                // Assuming your item class has an 'id' attribute
                val parent = parentFragment as? EpisodeSelectorDialog
                parent?.navigateToPlayback(episode.id)
            }
        }
    }
}