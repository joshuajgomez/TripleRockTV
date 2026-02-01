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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaLoadingFragment : Fragment() {

    companion object {
        fun newInstance() = MediaLoadingFragment()
    }

    private val viewModel: MediaLoadingViewModel by viewModels()

    lateinit var tvProgressVod: TextView
    lateinit var tvProgressLiveTv: TextView
    lateinit var tvProgressSeries: TextView
    lateinit var tvProgressEPG: TextView

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            listOf(
                tvProgressVod,
                tvProgressLiveTv,
                tvProgressSeries,
                tvProgressEPG,
            ).forEach {
                updateProgress(it)
                delay(100)
            }
            findNavController().navigate(R.id.action_mediaLoadingFragment_to_mainBrowseFragment)
        }
    }

    private suspend fun updateProgress(textView: TextView) {
        val progressDrawable = textView.background
        progressDrawable.level = 5000

        for (i in 0..100) {
            // Set level (percent * 100)
            progressDrawable.level = i * 100
            delay(50)
        }
    }
}