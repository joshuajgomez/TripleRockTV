package com.joshgm3z.triplerocktv.ui.details.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorViewModel
import com.joshgm3z.triplerocktv.databinding.DialogEpisodeSelectorBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class EpisodeSelectorDialog : DialogFragment() {

    private val viewModel: SeriesSelectorViewModel by viewModels()

    private lateinit var binding: DialogEpisodeSelectorBinding

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    private val args by navArgs<EpisodeSelectorDialogArgs>()

    private val seasonAdapter = SeasonAdapter {
        viewModel.onSeasonSelected(it.number)
    }

    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.episode_selector_dialog_height)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        episodeAdapter = EpisodeAdapter(glideUtil)
        episodeAdapter.onEpisodeClick = {
            SeriesSelectorFragmentDirections.toPlayback().apply {
                this.seriesId = args.seriesId
                this.streamId = it.id
                this.streamType = StreamType.Series
//                findNavController().navigate(this)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEpisodeSelectorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSeasonChips.adapter = seasonAdapter
        binding.rvEpisodes.adapter = episodeAdapter
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                updateUI(it)
            }
        }
    }

    private fun updateUI(uiState: SeriesSelectorUiState) {
        seasonAdapter.selectedSeasonNumber = uiState.selectedSeasonNumber
        if (seasonAdapter.seasons.isEmpty()) seasonAdapter.seasons = uiState.seasons
        episodeAdapter.episodes = uiState.episodes
        binding.rvEpisodes.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            context,
            R.anim.layout_fall_down
        )
        binding.rvEpisodes.scheduleLayoutAnimation()
        binding.rvEpisodes.post {
            binding.rvEpisodes.requestFocus()
        }
    }
}