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
        binding.btnAddMyList.setOnClickListener { viewModel.addToMyList() }
        binding.btnRemoveMyList.setOnClickListener { viewModel.removeFromMyList() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.streamData.collectLatest {
                it?.let { streamData ->
                    updateDetails(streamData)
                }
            }
        }
    }

    private fun updateDetails(streamData: StreamData) {
        handleBlur(streamData.movieMetadata?.backPosterUrl)
        binding.tvTitle.text = streamData.name
        binding.tvDescription.text = streamData.movieMetadata?.description
        binding.tvCast.text = "Cast: " + streamData.movieMetadata?.cast
        binding.tvDirector.text = "Director: " + streamData.movieMetadata?.director
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
        binding.tvGenre.text = streamData.movieMetadata?.genre
        binding.metadataView.subtitleDownloaded = !streamData.subtitleUrl.isNullOrEmpty()
        binding.metadataView.duration = streamData.movieMetadata?.totalDurationMs?.toTextTime()
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