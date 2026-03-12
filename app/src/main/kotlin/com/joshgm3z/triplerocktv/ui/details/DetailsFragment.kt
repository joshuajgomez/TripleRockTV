package com.joshgm3z.triplerocktv.ui.details

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.SparseArrayObjectAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.ui.browse.updateBackgroundWithBlur
import com.joshgm3z.triplerocktv.ui.streamcatalogue.alternateUri
import com.joshgm3z.triplerocktv.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : DetailsSupportFragment() {

    private val viewModel: DetailsViewModel by viewModels()
    private val args by navArgs<DetailsFragmentArgs>()

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var rowsAdapter: ArrayObjectAdapter

    private var backgroundImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailsBackground = DetailsSupportFragmentBackgroundController(this)

        val presenterSelector = ClassPresenterSelector()
        val detailsPresenter =
            FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            when (action.id) {
                ACTION_PLAY -> DetailsFragmentDirections.toPlayback(
                    args.streamId,
                    args.streamType
                ).apply { findNavController().navigate(this) }

                ACTION_RESUME -> DetailsFragmentDirections.toPlayback(
                    args.streamId,
                    args.streamType,
                ).apply {
                    resume = true
                    findNavController().navigate(this)
                }

                ACTION_FAVORITE -> viewModel.addToMyList()
                ACTION_REMOVE_FAVORITE -> viewModel.removeFromMyList()

                else -> return@OnActionClickedListener
            }
        }

        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        rowsAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = rowsAdapter
    }

    private fun handleBlur(imageUrl: String?) {
        imageUrl ?: return
        backgroundImageUrl = imageUrl
        if (viewModel.isBlurSettingEnabled)
            updateBackgroundWithBlur(requireContext(), imageUrl) {
                BackgroundManager.getInstance(requireActivity()).setBitmap(it)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.streamData.collectLatest {
                it?.let { streamData ->
                    updateDetails(streamData)
                }
            }
        }
        viewModel.fetchStreamDetails(args.streamId, args.streamType)
    }

    private fun updateDetails(streamData: StreamData) {
        Logger.debug("streamData = [${streamData}]")
        handleBlur(streamData.streamIcon)

        val existingRow = if (rowsAdapter.size() > 0) {
            rowsAdapter.get(0) as? DetailsOverviewRow
        } else {
            null
        }

        val detailsRow = if (existingRow != null) {
            // Reuse the existing row and update its data object
            existingRow.item = streamData
            existingRow
        } else {
            // Create a new row if it's the first time
            DetailsOverviewRow(streamData)
        }

        val actionAdapter = SparseArrayObjectAdapter()
        if (streamData.startedWatching) {
            actionAdapter.set(ACTION_RESUME.toInt(), Action(ACTION_RESUME, "Resume"))
            actionAdapter.set(ACTION_PLAY.toInt(), Action(ACTION_PLAY, "Start over"))
        } else {
            actionAdapter.set(ACTION_PLAY.toInt(), Action(ACTION_PLAY, "Play"))
        }
        if (streamData.inMyList) actionAdapter.set(
            ACTION_REMOVE_FAVORITE.toInt(),
            Action(ACTION_REMOVE_FAVORITE, "Remove from my list")
        ) else actionAdapter.set(
            ACTION_FAVORITE.toInt(),
            Action(ACTION_FAVORITE, "Add to my list")
        )

        detailsRow.actionsAdapter = actionAdapter

        Glide.with(requireContext())
            .asBitmap()
            .load(streamData.streamIcon.alternateUri(viewModel.serverUrl))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    detailsRow.setImageBitmap(requireContext(), resource)
                    rowsAdapter.notifyArrayItemRangeChanged(0, rowsAdapter.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        if (existingRow == null) {
            rowsAdapter.add(detailsRow)
        } else {
            // If it was an update, notify the adapter immediately for text/action changes
            rowsAdapter.notifyArrayItemRangeChanged(0, 1)
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundImageUrl ?: return
        lifecycleScope.launch {
            handleBlur(backgroundImageUrl)
        }
    }

    companion object {
        private const val ACTION_RESUME = 1L
        private const val ACTION_PLAY = 2L
        private const val ACTION_FAVORITE = 3L
        private const val ACTION_REMOVE_FAVORITE = 4L
    }
}
