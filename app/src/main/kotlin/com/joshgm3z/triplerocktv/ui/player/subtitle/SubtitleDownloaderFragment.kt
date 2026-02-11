package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    private val args: SubtitleDownloaderFragmentArgs by navArgs()

    private val viewModel: SubtitleDownloaderViewModel by viewModels()
    private val subtitleSelectorViewModel: SubtitleSelectorViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private var _binding: LayoutSubtitleDownloaderBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DownloadedSubtitleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.searchQuery(args.keyword)
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
        adapter = DownloadedSubtitleListAdapter().apply {
            binding.rvDefaultSubtitleList.adapter = this
            binding.rvDefaultSubtitleList.layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.subtitleUiState.collectLatest {
                Logger.debug("subtitleUiState = $it")
                adapter.subtitleList = it ?: emptyList()
                it?.let { subtitleList ->
                    binding.tvError.visibility = if (subtitleList.isEmpty()) VISIBLE else GONE
                }
                binding.tvTitle.text = when {
                    it == null -> "Searching subtitles"
                    it.isEmpty() -> "Subtitles"
                    else -> "Select a subtitle"
                }
            }
        }
        adapter.clickListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSubtitleClicked(subtitleData: SubtitleData) {
        lifecycleScope.launch {
            val url = viewModel.getSubtitleUrl(subtitleData.fileId)
            subtitleSelectorViewModel.subtitleToLoad.value = subtitleData.copy(url = url)
            delay(500)
            findNavController().popBackStack()
        }
    }
}