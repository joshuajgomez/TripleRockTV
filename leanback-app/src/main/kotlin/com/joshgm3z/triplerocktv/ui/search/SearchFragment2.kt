package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.viewmodel.SearchUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SearchViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentSearchBinding
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment2 : Fragment(), KeyboardViewListener, SimpleTextAdapter.ClickListener {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

    @Inject
    lateinit var streamAdapter: StreamAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                binding.rvHints.adapter = SimpleTextAdapter(it.searchHints, this@SearchFragment2)
                when (it.searchUiState) {
                    is SearchUiState.Initial -> showInitialUi()

                    is SearchUiState.Loading -> showLoadingUi()

                    is SearchUiState.Result -> updateSearchResult(it.searchUiState as SearchUiState.Result)
                }
            }
        }
    }

    private fun showLoadingUi() {
        streamAdapter.items = emptyList()
        binding.tvStatus.text = "Searching"
    }

    private fun showInitialUi() {
        streamAdapter.items = emptyList()
        binding.tvStatus.text = "Start typing to see results"
    }

    private fun initViews() {
        binding.keyboardView.listener = this
        binding.rvSearchList.adapter = streamAdapter
    }

    override fun onTextChange(text: String) {
        binding.etInput.setText(text)
        viewModel.onSearchInputChange(text)
    }

    override fun onClick(item: String) {
        binding.etInput.setText(item)
        viewModel.onSearchInputChange(item)
    }

    private fun updateSearchResult(result: SearchUiState.Result) {
        binding.tvStatus.text = "Search results for ${result.query}"
        streamAdapter.items = result.streamDataList
    }
}
