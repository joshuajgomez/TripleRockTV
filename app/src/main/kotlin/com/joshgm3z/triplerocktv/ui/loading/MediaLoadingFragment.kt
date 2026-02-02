package com.joshgm3z.triplerocktv.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaLoadingFragment : Fragment() {

    companion object {
        fun newInstance() = MediaLoadingFragment()
    }

    private val viewModel: MediaLoadingViewModel by viewModels()

    lateinit var tvProgressVod: TextView
    lateinit var tvProgressLiveTv: TextView
    lateinit var tvProgressSeries: TextView
    lateinit var tvProgressEPG: TextView
    lateinit var tvStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_media_loading,
            container,
            false
        )
        tvProgressVod = view.findViewById(R.id.tv_progress_vod)
        tvProgressLiveTv = view.findViewById(R.id.tv_progress_live_tv)
        tvProgressSeries = view.findViewById(R.id.tv_progress_series)
        tvProgressEPG = view.findViewById(R.id.tv_progress_epg)
        tvStatus = view.findViewById(R.id.tv_status)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                when (it) {
                    is MediaLoadingUiState.Initial -> {}
                    is MediaLoadingUiState.Error -> {}
                    is MediaLoadingUiState.Update -> {
                        it.map.forEach { (type, state) ->
                            when (type) {
                                MediaLoadingType.VideoOnDemand -> tvProgressVod
                                MediaLoadingType.LiveTv -> tvProgressLiveTv
                                MediaLoadingType.EPG -> tvProgressEPG
                                MediaLoadingType.Series -> tvProgressSeries
                            }.let { textView -> updateProgress(textView, state.percent) }
                        }
                        tvStatus.text = "Download complete"
                        delay(2000)
                        if (it.map.values.all { state -> state.percent == 100 })

                            findNavController().navigate(
                                R.id.action_mediaLoading_to_browse
                            )
                    }
                }
            }
        }
    }

    private fun updateProgress(textView: TextView, progress: Int) {
        val progressDrawable = textView.background
        progressDrawable.level = progress * 100
    }
}