package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.LayoutSubtitleDownloaderBinding
import com.joshgm3z.triplerocktv.repository.SubtitleData
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SubtitleDownloaderFragment : DialogFragment(), DownloadedSubtitleListClickListener {

    private val viewModel: SubtitleDownloaderViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private var _binding: LayoutSubtitleDownloaderBinding? = null
    private val binding get() = _binding!!

    private val adapter = DownloadedSubtitleListAdapter().apply {
        binding.rvDefaultSubtitleList.adapter = this
        binding.rvDefaultSubtitleList.layoutManager = LinearLayoutManager(context)
    }

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
        _binding = LayoutSubtitleDownloaderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.subtitleUiState.collectLatest {
                Logger.debug("subtitleUiState = $it")
                adapter.subtitleList = it.downloadedSubtitleList ?: emptyList()
                binding.tvError.visibility = if (adapter.subtitleList.isEmpty()) VISIBLE else GONE
            }
        }
        adapter.clickListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSubtitleClicked(subtitleData: SubtitleData) {
        viewModel.onSubtitleClicked(subtitleData)
        lifecycleScope.launch {
            delay(1000)
            findNavController().popBackStack()
        }
    }
}