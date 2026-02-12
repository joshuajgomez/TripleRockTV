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
import com.joshgm3z.triplerocktv.databinding.LayoutTrackSelectorBinding
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TrackSelectorFragment : DialogFragment(), TrackListClickListener {

    private val viewModel: TrackSelectorViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val args by navArgs<TrackSelectorFragmentArgs>()

    private var _binding: LayoutTrackSelectorBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackListAdapter

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
        binding.tvTitle.text = "Select ${args.trackType.name.lowercase()}"

        binding.findMoreButton.visibility =
            if (args.trackType == TrackType.Subtitle) View.VISIBLE else View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            when (args.trackType) {
                TrackType.Subtitle -> viewModel.subtitleTrackListState
                else -> viewModel.audioTrackListState
            }.collectLatest {
                Logger.debug("${args.trackType} trackListState = $it")
                if (!it.isNullOrEmpty()) adapter.trackList = it
            }
        }
        binding.findMoreButton.setOnClickListener {
            val action = TrackSelectorFragmentDirections.toSubtitleDownload()
            action.keyword = args.title
            findNavController().navigate(action)
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
}