package com.joshgm3z.triplerocktv.ui.details.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.toTextTime
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsUiState
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentDetailsBinding
import com.joshgm3z.triplerocktv.util.DimMode
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

    private var backgroundImageUrl: String? = null

    private var selectedEpisodeId = -1

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
        binding.flResumeContainer.setOnClickListener {
            DetailsFragmentDirections.toPlayback().apply {
                resume = true
                streamId = args.streamId
                streamType = args.streamType
                findNavController().navigate(this)
            }
        }
        binding.btnStartOver.setOnClickListener {
            DetailsFragmentDirections.toPlayback().apply {
                streamId = args.streamId
                streamType = args.streamType
                findNavController().navigate(this)
            }
        }
        binding.btnPlay.setOnClickListener {
            DetailsFragmentDirections.toPlayback().apply {
                streamId = args.streamId
                streamType = args.streamType
                findNavController().navigate(this)
            }
        }
        binding.btnMoreEpisodes.setOnClickListener {
            DetailsFragmentDirections.toEpisodeSelector().apply {
                seriesId = args.streamId
                initialSelectedEpisodeId = selectedEpisodeId
                findNavController().navigate(this)
            }
        }
        binding.btnAddMyList.setOnClickListener { viewModel.addToMyList() }
        binding.btnRemoveMyList.setOnClickListener { viewModel.removeFromMyList() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                it?.let { uiState ->
                    updateDetailsUiState(uiState)
                }
            }
        }
    }

    private fun updateDetailsUiState(uiState: DetailsUiState) {
        uiState.episodeId?.let {
            selectedEpisodeId = it
        }
        binding.tvTitle.text = uiState.title
        binding.metadataView.rating = uiState.rating
        binding.metadataView.subtitleDownloaded = !uiState.subtitle.isNullOrEmpty()

        binding.flResumeContainer.setVisible(uiState.progressPercent != null)
        binding.progressBar.progress = uiState.progressPercent ?: 0
        uiState.episodeLabel?.let {
            binding.btnResume.text = "Resume $it"
            binding.btnPlay.text = "Play $it"
        }
        binding.tvGenre.text = uiState.subtitle
        binding.btnStartOver.setVisible(uiState.progressPercent != null)
        binding.btnPlay.setVisible(uiState.progressPercent == null)

        binding.flResumeContainer.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                binding.progressBar.setVisible(hasFocus)
            }

        binding.btnRemoveMyList.setVisible(uiState.inMyList)
        binding.btnAddMyList.setVisible(!uiState.inMyList)
        binding.btnMoreEpisodes.setVisible(uiState.showMoreEpisodesButton)

        handleBlur(uiState.coverImage)
        binding.tvDescription.text = uiState.description
        binding.tvCast.text = uiState.cast
        binding.tvDirector.text = uiState.director
        binding.metadataView.duration = uiState.duration

        // handle focus
        if (uiState.progressPercent != null) {
            binding.flResumeContainer.requestFocus()
        } else {
            binding.btnPlay.requestFocus()
        }
    }

    private fun updateDetails(streamData: StreamData) {
        binding.tvTitle.text = streamData.name
        binding.flResumeContainer.setVisible(streamData.startedWatching)
        binding.btnStartOver.setVisible(streamData.startedWatching)
        binding.btnPlay.setVisible(!streamData.startedWatching)
        if (streamData.startedWatching) {
            binding.progressBar.progress = streamData.progressPercent()
            binding.flResumeContainer.requestFocus()
        } else {
            binding.btnPlay.requestFocus()
        }
        binding.flResumeContainer.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                binding.progressBar.setVisible(hasFocus)
            }
        binding.btnRemoveMyList.setVisible(streamData.inMyList)
        binding.btnAddMyList.setVisible(!streamData.inMyList)

        binding.metadataView.rating = streamData.rating
        binding.metadataView.subtitleDownloaded = !streamData.subtitleUrl.isNullOrEmpty()

        streamData.movieMetadata?.let { movieMetadata ->
            handleBlur(movieMetadata.backPosterUrl)

            if (!movieMetadata.description.isNullOrEmpty())
                binding.tvDescription.text = movieMetadata.description
            else binding.tvDescription.setVisible(false)

            if (!movieMetadata.cast.isNullOrEmpty())
                binding.tvCast.text = "Cast: " + movieMetadata.cast
            else binding.tvCast.setVisible(false)

            if (!movieMetadata.director.isNullOrEmpty())
                binding.tvDirector.text = "Director: " + movieMetadata.director
            else binding.tvDirector.setVisible(false)

            binding.tvGenre.text = movieMetadata.genre
            binding.tvGenre.setVisible(!movieMetadata.genre.isNullOrEmpty())

            binding.metadataView.duration = movieMetadata.totalDurationMs.toTextTime()
        }
    }

    private fun handleBlur(imageUrl: String?) {
        imageUrl ?: return
        if (backgroundImageUrl == imageUrl) return
        backgroundImageUrl = imageUrl
        glideUtil.getBitmap(uri = imageUrl, dimMode = DimMode.None) { bitmap ->
            if (!isVisible) return@getBitmap
            binding.ivBackdrop.setImageBitmap(bitmap)
        }
    }
}