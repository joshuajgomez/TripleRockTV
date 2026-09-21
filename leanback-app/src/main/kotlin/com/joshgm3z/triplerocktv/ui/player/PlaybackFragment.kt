package com.joshgm3z.triplerocktv.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreLogger
import com.joshgm3z.triplerocktv.core.viewmodel.PlaybackViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.TrackSelectorViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentPlayerBinding
import javax.inject.Inject

/**
 * A fragment for playing video content.
 */
@UnstableApi
@AndroidEntryPoint
class PlaybackFragment : Fragment() {

    private val viewModel: PlaybackViewModel by viewModels()

    private val trackViewModel: TrackSelectorViewModel by hiltNavGraphViewModels(
        R.id.nav_graph
    )

    lateinit var playerManager: PlayerManager

    private lateinit var binding: FragmentPlayerBinding

    @Inject
    lateinit var firestoreLogger: FirestoreLogger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerManager = PlayerManager(
            lifecycleOwner = viewLifecycleOwner,
            videoSupportFragment = childFragmentManager.findFragmentById(R.id.video_support_fragment)
                    as VideoSupportFragment,
            context = requireContext(),
            view = view,
            playbackViewModel = viewModel,
            trackSelectorViewModel = trackViewModel,
            navigate = {
                findNavController().navigate(it)
            },
            tvSkipForward = binding.tvSkipForward,
            tvSkipBack = binding.tvSkipBack,
            firestoreLogger = firestoreLogger
        )

        playerManager.playVideo(navArgs<PlaybackFragmentArgs>().value.resume)
    }
}
