package com.joshgm3z.triplerocktv.ui.streamcatalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.databinding.FragmentStreamCatalogueBinding
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.core.util.toTextTime
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueUiState
import com.joshgm3z.triplerocktv.core.viewmodel.CatalogueViewModel
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.ui.common.diffCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StreamCatalogueFragment : Fragment() {

    private val viewModel: CatalogueViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    lateinit var rowsAdapter: PagingDataAdapter<Any>

    private lateinit var binding: FragmentStreamCatalogueBinding

    private val args: StreamCatalogueFragmentArgs by navArgs()

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
            numberOfColumns = 4
        }
        rowsAdapter = PagingDataAdapter(streamPresenter, diffCallback)
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
                when (it) {
                    is CatalogueUiState.VideoOnDemand -> {
                        it.pagingStreams.collectLatest {
                            rowsAdapter.submitData(it as PagingData<Any>)
                        }
                    }

                    is CatalogueUiState.Series -> {
                        it.pagingStreams.collectLatest {
                            rowsAdapter.submitData(it as PagingData<Any>)
                        }
                    }

                    else -> return@collectLatest
                }
            }
        }
    }

    private fun updateStreamData(streamData: StreamData) {
        binding.includeDetails.tvTitle.text = streamData.name

        lifecycleScope.launch {
            val updatedMovieMetadata = viewModel.fetchMetadata(streamData.streamId) ?: return@launch

            binding.includeDetails.tvDescription.text = updatedMovieMetadata.description
            binding.includeDetails.metadataView.genre = updatedMovieMetadata.genre
            binding.includeDetails.metadataView.rating = streamData.rating
            binding.includeDetails.metadataView.duration = streamData.movieMetadata?.totalDurationMs?.toTextTime()
            /*glideUtil.loadImage(
                updatedMovieMetadata.backPosterUrl,
                binding.ivBackdrop
            )*/
        }
    }

    private fun updateSeriesStream(seriesStream: SeriesStream) {
        binding.includeDetails.tvTitle.text = seriesStream.name
        binding.includeDetails.tvDescription.text = seriesStream.plot
        /*glideUtil.loadImage(
            seriesStream.backdropUrl,
            binding.ivBackdrop
        )*/
        binding.includeDetails.metadataView.rating = seriesStream.rating.parseToFloat()
        binding.includeDetails.metadataView.genre = seriesStream.genre
        binding.includeDetails.metadataView.noOfSeasons = seriesStream.seasons?.size
        binding.includeDetails.metadataView.showMyList = seriesStream.favorite
    }

    private val clickListener = OnItemViewClickedListener { _, item, _, _ ->
        when (item) {
            is StreamData -> when (item.streamType) {
                StreamType.VideoOnDemand -> StreamCatalogueFragmentDirections.toDetails()
                    .apply {
                        streamId = item.streamId
                        streamType = item.streamType
                    }

                else -> StreamCatalogueFragmentDirections.toPlayback().apply {
                    streamId = item.streamId
                    streamType = item.streamType
                }
            }

            is SeriesStream -> StreamCatalogueFragmentDirections.toDetails().apply {
                streamId = item.seriesId
                streamType = StreamType.Series
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

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(
            ScreenName.Catalogue,
            mapOf("catalogue_streamType" to args.streamType.name)
        )
    }
}