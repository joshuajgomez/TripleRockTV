package com.joshgm3z.triplerocktv.ui.livetv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.PlaybackControlsRow
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.viewmodel.LiveTvViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentLiveTvCatalogueBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class LiveTvFragment : Fragment() {
    private val viewModel: LiveTvViewModel by viewModels()

    private lateinit var binding: FragmentLiveTvCatalogueBinding

    private lateinit var rowsAdapter: ArrayObjectAdapter

    @Inject
    lateinit var channelPresenter: ChannelPresenter

    private val programAdapter = ProgramAdapter()

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveTvCatalogueBinding.inflate(
            inflater,
            container,
            false
        )
        binding.rvPrograms.adapter = programAdapter
        initRowFragment()
        initPlayerFragment()
        return binding.root
    }

    private fun initPlayerFragment() {
        LeanbackPlayerAdapter(requireContext(), player, 16).apply {
            setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
            transportControlGlue = createControlGlue(this)
        }

        val videoSupportFragment =
            childFragmentManager.findFragmentById(R.id.video_support_fragment) as? VideoSupportFragment

        transportControlGlue.host = VideoSupportFragmentGlueHost(videoSupportFragment)
        player.playWhenReady = true
    }

    fun createControlGlue(
        adapter: LeanbackPlayerAdapter
    ): PlaybackTransportControlGlue<LeanbackPlayerAdapter> {
        return object : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(
            requireActivity(),
            adapter
        ) {
            override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {}
        }.apply {
            isControlsOverlayAutoHideEnabled = false
        }
    }

    private fun initRowFragment() {
        val gridFragment = VerticalGridSupportFragment()
        gridFragment.gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 1
        }
        rowsAdapter = ArrayObjectAdapter(channelPresenter)
        gridFragment.adapter = rowsAdapter
        gridFragment.onItemViewClickedListener = clickListener
        gridFragment.setOnItemViewSelectedListener(selectionListener)

        childFragmentManager.beginTransaction()
            .replace(binding.flProgramsContainer.id, gridFragment)
            .commit()
    }

    private val clickListener = OnItemViewClickedListener { _, item, _, _ ->
        val streamData = item as? StreamData ?: return@OnItemViewClickedListener
        findNavController().navigate(LiveTvFragmentDirections.toPlayback().apply {
            streamId = streamData.streamId
            streamType = StreamType.LiveTV
        })
    }

    private val selectionListener = OnItemViewSelectedListener { _, item, _, _ ->
        val streamData = item as? StreamData ?: return@OnItemViewSelectedListener
        viewModel.onStreamDataFocused(streamData)
    }

    private fun playVideo(videoUrl: String) {
        player.stop()
        player.clearMediaItems()

        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                it?.let {
                    rowsAdapter.setItems(it, null)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.programUiState.collectLatest {
                if (it == null) return@collectLatest

                it.videoToPlay?.let { videoUrl ->
                    playVideo(videoUrl)
                }
                it.programs.let { programs ->
                    programAdapter.programs = programs
                }
                binding.tvNoProgram.setVisible(it.programs.isEmpty())
                binding.rvPrograms.setVisible(!it.programs.isEmpty())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player.stop()
        player.clearMediaItems()
    }
}