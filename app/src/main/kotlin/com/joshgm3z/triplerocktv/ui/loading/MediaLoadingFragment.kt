package com.joshgm3z.triplerocktv.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.FragmentMediaLoadingBinding
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaLoadingFragment : Fragment() {

    private val viewModel: MediaLoadingViewModel by viewModels()
    private var _binding: FragmentMediaLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaLoadingBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                when (it) {
                    is MediaLoadingUiState.Initial -> {}
                    is MediaLoadingUiState.Error -> showError(it.message, it.summary)
                    is MediaLoadingUiState.Update -> {
                        it.map.forEach { (type, state) ->
                            when (type) {
                                MediaLoadingType.VideoOnDemand -> binding.pbwtVod
                                MediaLoadingType.LiveTv -> binding.pbwtLiveTv
                                MediaLoadingType.Series -> binding.pbwtSeries
                                MediaLoadingType.EPG -> binding.pbwtEpg
                            }.let { view -> view.loadingState = state }
                        }
                        if (it.map.values.all { state -> state.status == LoadingStatus.Complete }) {
                            binding.tvStatus.text = "Download complete"
                            delay(2000)
                            findNavController().navigate(
                                R.id.action_mediaLoading_to_browse
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showError(message: String, summary: String) {
        binding.errorCard.text = message + "\n" + summary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}