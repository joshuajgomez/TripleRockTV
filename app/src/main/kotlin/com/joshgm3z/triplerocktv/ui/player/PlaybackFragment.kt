package com.joshgm3z.triplerocktv.ui.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.semantics.error
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.ui.details.DetailsViewModel
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * A fragment for playing video content.
 */
@AndroidEntryPoint
class PlaybackFragment : VideoSupportFragment() {

    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var transportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
    private val args by navArgs<PlaybackFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackFragment)
        val playerAdapter = MediaPlayerAdapter(requireActivity())
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        transportControlGlue = PlaybackTransportControlGlue(requireActivity(), playerAdapter)
        transportControlGlue.host = glueHost

        viewModel.fetchStreamDetails(args.streamId)
        lifecycleScope.launch {
            viewModel.stream.collectLatest {
                it?.let {
                    transportControlGlue.title = it.name
                    transportControlGlue.playWhenPrepared()
                    playVideo(it.streamId, it.name)
//                    playerAdapter.setDataSource(Uri.parse(""))
                }
            }
        }
    }

    private fun playVideo(streamId: Int, streamName: String) {
        val videoUrl =
            "${Secrets.webUrl}/live/${Secrets.username}/${Secrets.password}/$streamId.m3u8"


    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }
}