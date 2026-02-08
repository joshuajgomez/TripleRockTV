package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.databinding.LayoutSubtitleSelectorBinding
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubtitleSelectorFragment : DialogFragment() {

    private val viewModel: SubtitleDownloaderViewModel by viewModels()

    private lateinit var binding: LayoutSubtitleSelectorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutSubtitleSelectorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.subtitleList.collectLatest {
                Logger.debug("subtitleList = $it")
                binding.subtitleDownloaderView.subtitleList = it
            }
        }
        binding.subtitleDownloaderView.registerClickListener {
            viewModel.onFindClicked("English 4k")
        }
        lifecycleScope.launch {
            delay(4000)
            viewModel.onFindClicked("English 4k")
        }
    }
}