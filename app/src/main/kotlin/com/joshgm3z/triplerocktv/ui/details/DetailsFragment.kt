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
import com.joshgm3z.triplerocktv.ui.browse.category.updateBackgroundWithBlur
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
            if (action.id == ACTION_PLAY) {
                val navAction = DetailsFragmentDirections.toPlayback(
                    args.streamId,
                    args.browseType
                )
                findNavController().navigate(navAction)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    updateDetails(it)
                }
            }
        }
        viewModel.fetchStreamDetails(args.streamId, args.browseType)
    }

    private fun updateDetails(uiState: DetailsUiState) {
        handleBlur(uiState.imageUrl)
        val detailsRow = DetailsOverviewRow(uiState)

        val actionAdapter = SparseArrayObjectAdapter()
        actionAdapter.set(ACTION_PLAY.toInt(), Action(ACTION_PLAY, "Play"))
        actionAdapter.set(ACTION_FAVORITE.toInt(), Action(ACTION_FAVORITE, "Add to favorite"))
        actionAdapter.set(
            ACTION_DOWNLOAD_SUBTITLE.toInt(),
            Action(ACTION_DOWNLOAD_SUBTITLE, "Download subtitle")
        )
        detailsRow.actionsAdapter = actionAdapter

        Glide.with(requireContext())
            .asBitmap()
            .load(uiState.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    detailsRow.setImageBitmap(requireContext(), resource)
                    rowsAdapter.notifyArrayItemRangeChanged(0, rowsAdapter.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        rowsAdapter.add(detailsRow)
    }

    override fun onResume() {
        super.onResume()
        backgroundImageUrl ?: return
        lifecycleScope.launch {
            handleBlur(backgroundImageUrl)
        }
    }

    companion object {
        private const val ACTION_PLAY = 1L
        private const val ACTION_FAVORITE = 2L
        private const val ACTION_DOWNLOAD_SUBTITLE = 3L
    }
}
