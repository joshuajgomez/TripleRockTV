package com.joshgm3z.triplerocktv.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.viewmodel.DownloaderUiState
import com.joshgm3z.triplerocktv.core.viewmodel.UpdaterViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentUpdaterBinding
import com.joshgm3z.triplerocktv.ui.common.UpdateItemView
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdaterFragment : Fragment() {
    private lateinit var binding: FragmentUpdaterBinding

    private val viewModel: UpdaterViewModel by viewModels()

    private var interceptBackPress = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdaterBinding.inflate(
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
                interceptBackPress = it.stateMap.values.any { state ->
                    state?.loadingStatus == LoadingStatus.Ongoing
                }
                binding.bvDownloadAll.isEnabled = it.enableButtons
                updateList(it)
            }
        }
        binding.bvDownloadAll.setOnClickListener {
            viewModel.startUpdate(
                StreamType.VideoOnDemand, StreamType.Series
            )
        }
        binding.ucvVod.setOnClickListener {
            viewModel.startUpdate(StreamType.VideoOnDemand)
        }
        binding.ucvSeries.setOnClickListener {
            viewModel.startUpdate(StreamType.Series)
        }
        setupBackstackListener()
        binding.bvDownloadAll.post {
            binding.bvDownloadAll.requestFocus()
        }
    }

    private fun updateList(uiState: DownloaderUiState) {
        Logger.debug("uiState = [${uiState}]")
        fun getView(streamType: StreamType): UpdateItemView = when (streamType) {
            StreamType.VideoOnDemand -> binding.ucvVod
            StreamType.Series -> binding.ucvSeries
            StreamType.LiveTV -> binding.ucvVod
        }

        uiState.stateMap.forEach { (streamType, state) ->
            val view = getView(streamType)
            view.isEnabled = uiState.enableButtons
            view.setVisible(true)
            view.showPlaceholder = state == null
            state ?: return@forEach

            view.showLoading = state.loadingStatus == LoadingStatus.Ongoing
            view.isSelected = state.loadingStatus == LoadingStatus.Ongoing
            view.showErrorStatus = state.loadingStatus == LoadingStatus.Error
            view.subtitle = when {
                state.filesCount == null -> state.status
                else -> getString(
                    R.string.dot_between_text,
                    state.filesCount, state.status
                )
            }
        }
    }

    private fun setupBackstackListener() = requireActivity()
        .onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(
                        if (interceptBackPress) UpdaterFragmentDirections.toDownloadCancelDialog()
                        else UpdaterFragmentDirections.toSplash()
                    )
                }
            }
        )

}