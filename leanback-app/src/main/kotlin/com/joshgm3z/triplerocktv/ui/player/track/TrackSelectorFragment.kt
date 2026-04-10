package com.joshgm3z.triplerocktv.ui.player.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.SubtitleData
import com.joshgm3z.triplerocktv.databinding.LayoutTrackSelectorBinding
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.viewmodel.ListState
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.core.viewmodel.TrackInfo
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TrackSelectorFragment : DialogFragment(),
    TrackListClickListener, DownloadedSubtitleListClickListener {

    private val viewModel: TrackSelectorViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val args by navArgs<TrackSelectorFragmentArgs>()

    private var _binding: LayoutTrackSelectorBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackListAdapter

    private lateinit var downloadedSubtitleAdapter: DownloadedSubtitleListAdapter

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.container_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.container_height)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutTrackSelectorBinding.inflate(inflater)
        adapter = TrackListAdapter().apply {
            binding.rvTrackList.adapter = this
            binding.rvTrackList.layoutManager = LinearLayoutManager(context)
            clickListener = this@TrackSelectorFragment
        }
        downloadedSubtitleAdapter = DownloadedSubtitleListAdapter().apply {
            binding.rvOnlineSubtitleList.adapter = this
            binding.rvOnlineSubtitleList.layoutManager = LinearLayoutManager(context)
            clickListener = this@TrackSelectorFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                Logger.debug("uiState = $it")
                binding.llLoadingOverlay.setVisible(it.isLoading)
                binding.tvFindMoreButton.setVisible(it.listState is ListState.SubtitleTracks)

                when (it.listState) {
                    is ListState.SubtitleTracks -> {
                        binding.tvTitle.text = "Subtitles tracks"
                        showTracks((it.listState as ListState.SubtitleTracks).list)
                    }

                    is ListState.AudioTracks -> {
                        binding.tvTitle.text = "Audio tracks"
                        showTracks((it.listState as ListState.AudioTracks).list)
                    }

                    is ListState.OnlineSubtitleTracks -> {
                        binding.tvTitle.text = getText(R.string.open_subtitles)
                        showOnlineSubtitles((it.listState as ListState.OnlineSubtitleTracks).list)
                    }

                    else -> return@collectLatest
                }
            }
        }
        binding.tvFindMoreButton.setOnClickListener {
            viewModel.onFindMoreClicked(args.title)
        }
    }

    private fun showOnlineSubtitles(list: List<SubtitleData>) {
        downloadedSubtitleAdapter.subtitleList = list
        binding.rvOnlineSubtitleList.setVisible(true)
        binding.rvTrackList.setVisible(false)
    }

    private fun showTracks(list: List<TrackInfo>) {
        adapter.trackList = list
        binding.rvOnlineSubtitleList.setVisible(false)
        binding.rvTrackList.setVisible(true)
        binding.rvTrackList.post {
            binding.rvTrackList.layoutManager
                ?.findViewByPosition(list.indexOfFirst { it.isSelected })
                ?.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTrackClicked(trackInfo: TrackInfo) {
        if (!trackInfo.isSelected) viewModel.onTrackClicked(trackInfo)
        lifecycleScope.launch {
            delay(1000)
            findNavController().popBackStack()
        }
    }

    override fun onSubtitleClicked(subtitleData: SubtitleData) {
        viewModel.onDownloadedSubtitleClick(subtitleData)
    }
}