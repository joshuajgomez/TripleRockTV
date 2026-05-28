package com.joshgm3z.triplerocktv.ui.details.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SeriesSelectorViewModel
import com.joshgm3z.triplerocktv.databinding.DialogEpisodeSelectorBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class EpisodeSelectorDialog : DialogFragment() {

    private val viewModel: SeriesSelectorViewModel by viewModels()

    private val args by navArgs<SeriesDetailsFragmentArgs>()

    private lateinit var binding: DialogEpisodeSelectorBinding

    @Inject
    lateinit var glideUtil: GlideUtil

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEpisodeSelectorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                updateUI(it)
            }
        }
    }

    private fun updateUI(uiState: SeriesSelectorUiState) {

    }
}