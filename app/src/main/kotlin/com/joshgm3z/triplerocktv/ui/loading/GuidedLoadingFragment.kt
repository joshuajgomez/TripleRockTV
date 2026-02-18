package com.joshgm3z.triplerocktv.ui.loading

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.StreamType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class GuidedLoadingFragment : GuidedStepSupportFragment() {

    private val viewModel: MediaLoadingViewModel by viewModels()

    companion object {
        val idVod = 0L
        val idSeries = 1L
        val idLiveTv = 2L
        val idEpg = 3L
        val idStatus = 4L
        var focusedId = -1L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                when (it) {
                    is MediaLoadingUiState.Initial -> {}
                    is MediaLoadingUiState.Error -> findNavController().navigate(
                        GuidedLoadingFragmentDirections.toError("${it.message}\n${it.summary}")
                    )

                    is MediaLoadingUiState.Update -> {
                        it.map.forEach { (type, state) ->
                            when (type) {
                                StreamType.VideoOnDemand -> idVod
                                StreamType.LiveTV -> idLiveTv
                                StreamType.Series -> idSeries
                            }.let { id -> updateStatus(id, state) }
                        }
                        if (it.map.values.all { state -> state.status == LoadingStatus.Error || state.status == LoadingStatus.Complete }) {
                            focusedId = -1
                            showOverallStatus(
                                message = "Download complete",
                                icon = R.drawable.ic_check_circle_green
                            )
                            delay(2000)
                            findNavController().navigate(GuidedLoadingFragmentDirections.toBrowse())
                        }
                    }
                }
            }
        }
    }

    fun updateStatus(
        actionId: Long,
        loadingState: LoadingState
    ) {
        val toInt = actionId.toInt()
        val action = actions[toInt]
        when (loadingState.status) {
            LoadingStatus.Initial -> "Waiting"
            LoadingStatus.Ongoing -> {
                focusedId = actionId
                "Downloading ${loadingState.percent}%"
            }

            LoadingStatus.Complete -> "Downloaded"
            LoadingStatus.Error -> "Download error"
        }.let { action.description = it }

        notifyActionChanged(toInt)
    }

    private fun showOverallStatus(
        message: String? = null,
        description: String? = null,
        icon: Int? = null
    ) {
        val action = actions[idStatus.toInt()]
        action.title = message ?: ""
        action.description = description ?: ""
        action.icon = if (icon == null) null
        else ContextCompat.getDrawable(requireContext(), icon)
        notifyActionChanged(idStatus.toInt())
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Updating content",
            "Please wait while app gets latest content from IPTV server",
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idVod)
                .title("Video on demand")
                .description("Waiting")
                .focusable(false)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idSeries)
                .title("Series")
                .description("Waiting")
                .focusable(false)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idLiveTv)
                .title("Live TV")
                .description("Waiting")
                .focusable(false)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idEpg)
                .title("Guide")
                .description("Waiting")
                .focusable(false)
                .build()
        )

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idStatus)
                .title("")
                .focusable(false) // Prevents user from selecting it
                .infoOnly(true)   // Styles it as informational text
                .multilineDescription(true)
                .build()
        )
    }

    override fun onCreateActionsStylist(): GuidedActionsStylist {
        return LoadingActionsStylist()
    }

}