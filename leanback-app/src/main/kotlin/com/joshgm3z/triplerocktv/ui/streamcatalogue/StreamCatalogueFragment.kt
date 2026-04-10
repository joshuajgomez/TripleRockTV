package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.databinding.FragmentStreamCatalogueBinding
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.toTextTime
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.core.viewmodel.StreamViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StreamCatalogueFragment : Fragment() {

    private val viewModel: StreamViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    lateinit var rowsAdapter: ArrayObjectAdapter

    private lateinit var binding: FragmentStreamCatalogueBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStreamCatalogueBinding.inflate(inflater)
        initRowFragment()
        return binding.root
    }

    private fun initRowFragment() {
        val gridFragment = VerticalGridSupportFragment()
        gridFragment.gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 5
        }
        rowsAdapter = ArrayObjectAdapter(streamPresenter)
        gridFragment.adapter = rowsAdapter
        gridFragment.onItemViewClickedListener = clickListener
        gridFragment.setOnItemViewSelectedListener(selectionListener)

        childFragmentManager.beginTransaction()
            .replace(binding.flStreamRowContainer.id, gridFragment)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                it?.let {
                    rowsAdapter.setItems(it, null)
                }
            }
        }
    }

    private fun updateStreamData(streamData: StreamData) {
        binding.tvTitle.text = streamData.name

        lifecycleScope.launch {
            val updatedMovieMetadata = viewModel.fetchMetadata(streamData.streamId) ?: return@launch

            binding.tvDescription.text = updatedMovieMetadata.description
            binding.tvCast.text = "Cast: ${updatedMovieMetadata.cast}"
            binding.tvCast.setVisible(updatedMovieMetadata.cast?.isNotEmpty())
            binding.tvDirector.text = "Directed by: ${updatedMovieMetadata.director}"
            binding.tvDirector.setVisible(updatedMovieMetadata.director?.isNotEmpty())
            binding.metadataView.genre = updatedMovieMetadata.genre
            binding.metadataView.rating = streamData.rating
            binding.metadataView.duration = streamData.movieMetadata?.totalDurationMs?.toTextTime()
            glideUtil.loadImage(
                updatedMovieMetadata.backPosterUrl,
                binding.ivBackdrop
            )
        }
    }

    private fun updateSeriesStream(seriesStream: SeriesStream) {
        binding.tvTitle.text = seriesStream.name
        binding.tvDescription.text = seriesStream.plot
        glideUtil.loadImage(
            seriesStream.backdropUrl,
            binding.ivBackdrop
        )
        binding.tvCast.text = "Cast: ${seriesStream.cast}"
        binding.tvCast.setVisible(seriesStream.cast?.isNotEmpty())
        binding.tvDirector.text = "Directed by: ${seriesStream.director}"
        binding.tvDirector.setVisible(seriesStream.director?.isNotEmpty())
        binding.metadataView.rating = seriesStream.rating.parseToFloat()
        binding.metadataView.genre = seriesStream.genre
        binding.metadataView.seasonCount = seriesStream.seasons?.size
        binding.metadataView.showMyList = seriesStream.inMyList
    }

    private val clickListener = OnItemViewClickedListener { _, item, _, _ ->
        when (item) {
            is StreamData -> StreamCatalogueFragmentDirections.toDetails().apply {
                streamId = item.streamId
                streamType = item.streamType
            }

            is SeriesStream -> StreamCatalogueFragmentDirections.toSeriesDetails().apply {
                seriesId = item.seriesId
            }

            else -> return@OnItemViewClickedListener
        }.let {
            findNavController().navigate(it)
        }
    }

    private val selectionListener = OnItemViewSelectedListener { _, item, _, _ ->
        when (item) {
            is StreamData -> updateStreamData(item)
            is SeriesStream -> updateSeriesStream(item)
            else -> return@OnItemViewSelectedListener
        }
    }

}