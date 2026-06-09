package com.joshgm3z.triplerocktv.ui.details.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsUiState
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentDetailsBinding
import com.joshgm3z.triplerocktv.ui.common.DelayedTextView
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
        binding.flResumeContainer.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                binding.progressBar.setVisible(hasFocus)
            }
        binding.btnStartOver.setOnClickListener {
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
        binding.btnPlay.setOnClickListener {
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
        binding.btnMoreEpisodes.setOnClickListener {
            DetailsFragmentDirections.toEpisodeSelector().apply {
                seriesId = args.streamId
                initialSelectedEpisodeId = selectedEpisodeId
                findNavController().navigate(this)
            }
        }
        binding.btnAddMyList.setOnClickListener { viewModel.addToMyList(args.streamType) }
        binding.btnRemoveMyList.setOnClickListener { viewModel.removeFromMyList(args.streamType) }
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
        uiState.episodeLabel?.let {
            binding.btnResume.text = "Resume $it"
            binding.btnPlay.text = "Play $it"
        }
        handleBlur(uiState.coverImage)
        binding.progressBar.progress = uiState.progressPercent ?: 0

        binding.metadataView.subtitleDownloaded = uiState.subtitleDownloaded
        binding.metadataView.rating = uiState.rating
        binding.metadataView.showMyList = uiState.inMyList
        binding.metadataView.duration = uiState.duration
        binding.metadataView.noOfSeasons = uiState.noOfSeasons

        binding.tvTitle.text = uiState.title
        binding.tvGenre.text(uiState.subtitle)
        binding.tvDescription.text(uiState.description)
        binding.tvCast.text(uiState.cast)
        binding.tvDirector.text(uiState.director)

        // button visibility
        if (!uiState.showButtons) return
        binding.flResumeContainer.setVisible(uiState.progressPercent != null)
        binding.btnStartOver.setVisible(uiState.progressPercent != null)
        binding.btnPlay.setVisible(uiState.progressPercent == null)
        binding.btnRemoveMyList.setVisible(uiState.inMyList)
        binding.btnAddMyList.setVisible(!uiState.inMyList)
        binding.btnMoreEpisodes.setVisible(uiState.showMoreEpisodesButton)

        // handle focus
        if (uiState.progressPercent != null) {
            binding.flResumeContainer.requestFocus()
        } else {
            binding.btnPlay.requestFocus()
        }
    }

    private fun DelayedTextView.text(value: String?) {
        text = value
        if (value == null) return
        setVisible(!value.isEmpty())
    }

    private fun handleBlur(imageUrl: String?) {
        imageUrl ?: return
        backgroundImageUrl = imageUrl
        glideUtil.getBitmap(uri = imageUrl, dimMode = DimMode.None) { bitmap ->
            if (!isVisible) return@getBitmap
            binding.ivBackdrop.setImageBitmap(bitmap)
        }
    }

    override fun onResume() {
        super.onResume()
        handleBlur(backgroundImageUrl)
    }
}