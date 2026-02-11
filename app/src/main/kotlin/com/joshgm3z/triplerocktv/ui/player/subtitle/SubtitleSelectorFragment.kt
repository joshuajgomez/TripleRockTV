package com.joshgm3z.triplerocktv.ui.player.subtitle

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
import com.joshgm3z.triplerocktv.databinding.LayoutSubtitleSelectorBinding
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SubtitleSelectorFragment : DialogFragment() {

    private val viewModel: SubtitleDownloaderViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private val args by navArgs<SubtitleSelectorFragmentArgs>()

    private var _binding: LayoutSubtitleSelectorBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SubtitleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutSubtitleSelectorBinding.inflate(inflater)
        adapter = SubtitleListAdapter().apply {
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
                binding.subtitleDownloaderView.subtitleList = it.downloadedSubtitleList
                adapter.subtitleList = it.defaultSubtitleList ?: emptyList()
            }
        }
        binding.subtitleDownloaderView.listenFindButtonClick {
            viewModel.onFindClicked(args.title)
        }
        binding.subtitleDownloaderView.listenSubtitleClick {
            viewModel.onSubtitleClicked(it)
            lifecycleScope.launch {
                delay(1000)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}