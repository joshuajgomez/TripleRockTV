package com.joshgm3z.triplerocktv.ui.details

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.add
import androidx.fragment.app.viewModels
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.SparseArrayObjectAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.DimMode
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.Logger
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SeriesDetailsFragment : DetailsSupportFragment() {

    private val viewModel: SeriesDetailsViewModel by viewModels()

    private val args by navArgs<SeriesDetailsFragmentArgs>()

    @Inject
    lateinit var glideUtil: GlideUtil

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var rowsAdapter: ArrayObjectAdapter

    private var backgroundImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailsBackground = DetailsSupportFragmentBackgroundController(this)

        val presenterSelector = ClassPresenterSelector()
        val detailsPresenter =
            FullWidthDetailsOverviewRowPresenter(SeriesDetailsDescriptionPresenter())

        detailsPresenter.backgroundColor = ContextCompat.getColor(requireContext(), R.color.gray)

        detailsPresenter.onActionClickedListener = onActionClickedListener

        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        rowsAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = rowsAdapter
    }

    val onActionClickedListener = OnActionClickedListener { action ->
        when (action.id) {
            ACTION_PLAY -> DetailsFragmentDirections.toPlayback(
                args.seriesId,
                StreamType.Series
            ).apply { findNavController().navigate(this) }

            ACTION_RESUME -> DetailsFragmentDirections.toPlayback(
                args.seriesId,
                StreamType.Series
            ).apply {
                resume = true
                findNavController().navigate(this)
            }

            ACTION_FAVORITE -> viewModel.addToMyList()
            ACTION_REMOVE_FAVORITE -> viewModel.removeFromMyList()

            else -> return@OnActionClickedListener
        }
    }

    private fun handleBlur(imageUrl: String?) {
        imageUrl ?: return
        if (backgroundImageUrl == imageUrl) return
        backgroundImageUrl = imageUrl
        glideUtil.getBitmap(uri = imageUrl, dimMode = DimMode.Dark) { bitmap ->
            if (!isVisible) return@getBitmap
            requireActivity().setBackground(bitmap)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.seriesStream.collectLatest {
                it?.let { seriesStream ->
                    updateDetails(seriesStream)
                }
            }
        }
        viewModel.fetchStreamDetails(args.seriesId)
    }

    private fun updateDetails(seriesStream: SeriesStream) {
        Logger.debug("streamData = [${seriesStream}]")
        handleBlur(seriesStream.backdropUrl)

        val existingRow = if (rowsAdapter.size() > 0) {
            rowsAdapter.get(0) as? DetailsOverviewRow
        } else {
            null
        }

        val detailsRow = if (existingRow != null) {
            // Reuse the existing row and update its data object
            existingRow.item = seriesStream
            existingRow
        } else {
            // Create a new row if it's the first time
            DetailsOverviewRow(seriesStream)
        }

        glideUtil.getBitmap(seriesStream.coverImageUrl) {
            detailsRow.setImageBitmap(requireContext(), it)
            rowsAdapter.notifyArrayItemRangeChanged(0, rowsAdapter.size())
        }

        if (existingRow == null) {
            rowsAdapter.add(detailsRow)
        } else {
            // If it was an update, notify the adapter immediately for text/action changes
            rowsAdapter.notifyArrayItemRangeChanged(0, 1)
        }

        showSeasons(seriesStream)
    }

    private fun showSeasons(seriesStream: SeriesStream) {
        if (rowsAdapter.size() > 1) {
            rowsAdapter.removeItems(1, rowsAdapter.size() - 1)
        }

        val episodePresenter = EpisodePresenter(glideUtil) // Create this class to define how episode cards look

        seriesStream.seasons?.forEachIndexed { index, season ->
            val listRowAdapter = ArrayObjectAdapter(episodePresenter)

            season.episodes.forEach { episode ->
                listRowAdapter.add(episode)
            }

            val header = HeaderItem(index.toLong(), "Season ${season.number}")
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundImageUrl ?: return
        glideUtil.getBitmap(uri = backgroundImageUrl, dimMode = DimMode.Dark) { bitmap ->
            if (!isVisible) return@getBitmap
            requireActivity().setBackground(bitmap)
        }
    }

    companion object {
        private const val ACTION_RESUME = 1L
        private const val ACTION_PLAY = 2L
        private const val ACTION_FAVORITE = 3L
        private const val ACTION_REMOVE_FAVORITE = 4L
    }
}
