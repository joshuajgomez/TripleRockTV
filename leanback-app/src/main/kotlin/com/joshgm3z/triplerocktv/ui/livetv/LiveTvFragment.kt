package com.joshgm3z.triplerocktv.ui.livetv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.toTextTime
import com.joshgm3z.triplerocktv.core.viewmodel.LiveTvViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentLiveTvBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@AndroidEntryPoint
class LiveTvFragment : Fragment() {
    private lateinit var binding: FragmentLiveTvBinding

    private val viewModel: LiveTvViewModel by viewModels()

    @Inject
    lateinit var channelAdapter: ChannelAdapter

    @Inject
    lateinit var glideUtil: GlideUtil

    private val timeFrameAdapter = TimeFrameAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveTvBinding.inflate(inflater, container, false)
        binding.rvChannels.adapter = channelAdapter
        binding.rvTimeList.adapter = timeFrameAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                Logger.debug("uiState = [$it]")
                if (it.channels.isEmpty()) return@collectLatest

                binding.ivPoster.setVisible(true)
                binding.placeholder.root.setVisible(false)
                binding.timeMarkerView.setVisible(true)
                channelAdapter.setChannels(
                    it.timeFrames.first(),
                    it.timeFrames.last(),
                    it.currentTime,
                    it.channels,
                )
                timeFrameAdapter.timeFrames = it.timeFrames
                it.activeProgram?.let { program ->
                    updateFocusedProgram(program)
                }
                binding.tvCurrentDate.text = it.currentTime.toTextTime()
                binding.timeMarkerView.timeText = it.currentTime.toTextTime("hh:mm")

                val minutesPassed = Duration
                    .between(it.timeFrames.first(), it.currentTime)
                    .toMinutes()
                    .toInt()
                binding.timeMarkerView.minutesOffset = minutesPassed
            }
        }
        channelAdapter.onChannelClicked = { channel ->
            findNavController().navigate(
                LiveTvFragmentDirections.toPlayback().apply {
                    this.streamId = channel.streamId
                    this.streamType = StreamType.LiveTV
                }
            )
        }
        channelAdapter.onChannelFocused = { channel ->
            channel.programs.firstOrNull()?.let {
                updateFocusedProgram(it)
            }
        }
    }

    private fun updateFocusedProgram(program: XmlTvProgram) {
        binding.tvNowPlaying.setVisible(true)
        binding.tvTitle.text = program.title
        binding.tvDescription.text = program.description
        binding.tvProgramTime.text =
            "${program.start.toTextTime("hh:mm a")} to ${program.stop.toTextTime("hh:mm a")}"
        glideUtil.loadImage(
            program.icon,
            binding.ivPoster,
            R.drawable.default_program_poster
        )
    }
}