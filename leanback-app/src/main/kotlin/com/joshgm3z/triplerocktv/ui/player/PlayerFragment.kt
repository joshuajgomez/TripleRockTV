package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreLogger
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsUiState
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentPlayerBinding
import com.joshgm3z.triplerocktv.ui.common.DelayedTextView
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A fragment for playing video content.
 */
@UnstableApi
@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding

    private val playbackViewModel: PlaybackViewModel by viewModels()

    private val trackViewModel: TrackSelectorViewModel by hiltNavGraphViewModels(
        R.id.nav_graph
    )

    private val detailsViewModel: DetailsViewModel by viewModels()

    lateinit var playerManager: PlayerManager

    @Inject
    lateinit var firestoreLogger: FirestoreLogger

    @Inject
    lateinit var glideUtil: GlideUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerManager = PlayerManager(
            lifecycleOwner = viewLifecycleOwner,
            videoSupportFragment = childFragmentManager.findFragmentById(R.id.video_support_fragment)
                    as VideoSupportFragment,
            context = requireContext(),
            view = view,
            playbackViewModel = playbackViewModel,
            trackSelectorViewModel = trackViewModel,
            navigate = {
                findNavController().navigate(it)
            },
            tvSkipForward = binding.tvSkipForward,
            tvSkipBack = binding.tvSkipBack,
            firestoreLogger = firestoreLogger,
            onPlaybackStarted = {
                binding.layoutFragmentDetails.root.setVisible(false)
            }
        )
        val buttons = listOf(
            binding.layoutFragmentDetails.bvPlay,
            binding.layoutFragmentDetails.bvResume,
            binding.layoutFragmentDetails.bvAddMyList,
            binding.layoutFragmentDetails.bvRemoveMyList,
        )
        binding.layoutFragmentDetails.bvPlay.setOnClickListener {
            buttons.forEach {
                it.setVisible(false)
            }
            playerManager.playVideo()
        }
        binding.layoutFragmentDetails.bvResume.setOnClickListener {
            buttons.forEach {
                it.setVisible(false)
            }
            playerManager.playVideo(resume = true)
        }
        lifecycleScope.launch {
            detailsViewModel.uiState.collectLatest {
                it?.let {
                    updateDetailsUiState(it)
                }
            }
        }
    }

    private fun updateDetailsUiState(uiState: DetailsUiState) {
        binding.layoutFragmentDetails.bvResume.progress = uiState.progressPercent ?: 0

        binding.layoutFragmentDetails.includeDetails.metadataView.subtitleDownloaded =
            uiState.subtitleDownloaded
        binding.layoutFragmentDetails.includeDetails.metadataView.rating = uiState.rating
        binding.layoutFragmentDetails.includeDetails.metadataView.duration = uiState.duration
        binding.layoutFragmentDetails.includeDetails.metadataView.noOfSeasons = uiState.noOfSeasons

        binding.layoutFragmentDetails.includeDetails.tvTitle.text = uiState.title
        binding.layoutFragmentDetails.includeDetails.tvCategoryName.text = uiState.categoryName
        binding.layoutFragmentDetails.includeDetails.tvCategoryName.setVisible(true)
        binding.layoutFragmentDetails.includeDetails.tvGenre.text(uiState.subtitle)
        binding.layoutFragmentDetails.includeDetails.tvDescription.text(uiState.description)
        binding.layoutFragmentDetails.includeDetails.tvCast.text(uiState.cast)
        binding.layoutFragmentDetails.includeDetails.tvDirector.text(uiState.director)

        // button visibility
        if (!uiState.showButtons) return
        binding.layoutFragmentDetails.bvResume.setVisible(uiState.progressPercent != null)
        binding.layoutFragmentDetails.bvStartOver.setVisible(uiState.progressPercent != null)
        binding.layoutFragmentDetails.bvPlay.setVisible(uiState.progressPercent == null)
        binding.layoutFragmentDetails.bvRemoveMyList.setVisible(uiState.favorite)
        binding.layoutFragmentDetails.bvAddMyList.setVisible(!uiState.favorite)
        binding.layoutFragmentDetails.bvMoreEpisodes.setVisible(uiState.showMoreEpisodesButton)

        // handle focus
        if (uiState.progressPercent != null) {
            binding.layoutFragmentDetails.bvResume.requestFocus()
        } else {
            binding.layoutFragmentDetails.bvPlay.requestFocus()
        }

        glideUtil.loadImage(
            url = uiState.coverImage,
            imageView = binding.layoutFragmentDetails.ivBackdrop,
        )
    }

    private fun DelayedTextView.text(value: String?) {
        text = value
        if (value == null) return
        setVisible(!value.isEmpty())
    }
}
