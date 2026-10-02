package com.joshgm3z.triplerocktv.ui.details.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsUiState
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentDetailsBinding
import com.joshgm3z.triplerocktv.ui.common.DelayedTextView
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding

    private val viewModel: DetailsViewModel by viewModels()

    private val args by navArgs<DetailsFragmentArgs>()

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    private var selectedEpisodeId = -1

    private var initialUiUpdated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(LayoutInflater.from(context))
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.bvResume.setOnClickListener {
            DetailsFragmentDirections.toPlayback().apply {
                resume = true
                streamType = args.streamType
                if (args.streamType == StreamType.Series) {
                    streamId = selectedEpisodeId
                    seriesId = args.streamId
                } else {
                    streamId = args.streamId
                }
                findNavController().navigate(this)
            }
        }
        binding.bvStartOver.setOnClickListener {
            DetailsFragmentDirections.toPlayback().apply {
                streamType = args.streamType
                if (args.streamType == StreamType.Series) {
                    streamId = selectedEpisodeId
                    seriesId = args.streamId
                } else {
                    streamId = args.streamId
                }
                findNavController().navigate(this)
            }
        }
        binding.bvPlay.setOnClickListener {
            DetailsFragmentDirections.toPlayback().apply {
                streamType = args.streamType
                if (args.streamType == StreamType.Series) {
                    streamId = selectedEpisodeId
                    seriesId = args.streamId
                } else {
                    streamId = args.streamId
                }
                findNavController().navigate(this)
            }
        }
        binding.bvMoreEpisodes.setOnClickListener {
            DetailsFragmentDirections.toEpisodeSelector().apply {
                seriesId = args.streamId
                initialSelectedEpisodeId = selectedEpisodeId
                findNavController().navigate(this)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    it?.let { uiState ->
                        updateDetailsUiState(uiState)
                    }
                }
            }
        }
    }

    private fun updateDetailsUiState(uiState: DetailsUiState) {
        uiState.episodeId?.let {
            selectedEpisodeId = it
        }
        uiState.episodeLabel?.let {
            binding.bvResume.text = "Resume $it"
            binding.bvPlay.text = "Play $it"
        }

        binding.includeDetails.metadataView.subtitleDownloaded = uiState.subtitleDownloaded
        binding.includeDetails.metadataView.rating = uiState.rating
        binding.includeDetails.metadataView.duration = uiState.duration
        binding.includeDetails.metadataView.noOfSeasons = uiState.noOfSeasons

        binding.includeDetails.tvTitle.text = uiState.title
        binding.includeDetails.tvCategoryName.text = uiState.categoryName
        binding.includeDetails.tvCategoryName.setVisible(true)
        binding.includeDetails.tvGenre.text(uiState.subtitle)
        binding.includeDetails.tvDescription.text(uiState.description)
        binding.includeDetails.tvCast.text(uiState.cast)
        binding.includeDetails.tvCastLabel.setVisible(!uiState.cast.isNullOrEmpty())
        binding.includeDetails.tvDirector.text(uiState.director)
        binding.includeDetails.tvDirectorLabel.setVisible(!uiState.director.isNullOrEmpty())

        // button visibility
        if (!uiState.showButtons) return

        if (!initialUiUpdated) glideUtil.loadImage(
            url = uiState.coverImage,
            error = R.drawable.backdrop_placeholder,
            imageView = binding.ivBackdrop
        )
        binding.bvResume.progress = uiState.progressPercent ?: 0
        binding.bvResume.setVisible(uiState.progressPercent != null)
        binding.bvStartOver.setVisible(uiState.progressPercent != null)
        binding.bvPlay.setVisible(uiState.progressPercent == null)
        binding.bvMoreEpisodes.setVisible(uiState.showMoreEpisodesButton)
        binding.bvAddMyList.text = if (uiState.favorite) "Remove favorite" else "Add favorite"
        when {
            uiState.favorite -> R.drawable.baseline_star_24
            else -> R.drawable.outline_star_outline_24
        }.let {
            ContextCompat.getDrawable(requireContext(), it)
        }?.let { drawable ->
            binding.bvAddMyList.drawable = drawable
        }
        binding.bvAddMyList.setOnClickListener { viewModel.updateMyList(!uiState.favorite) }
        binding.bvAddMyList.setVisible(true)

        // handle focus
        if (!initialUiUpdated) if (uiState.progressPercent != null) {
            binding.bvResume.requestFocus()
        } else {
            binding.bvPlay.requestFocus()
        }
        initialUiUpdated = true
    }

    private fun DelayedTextView.text(value: String?) {
        text = value
        if (value == null) return
        setVisible(!value.isEmpty())
    }

    override fun onPause() {
        super.onPause()
        initialUiUpdated = false
    }
}
