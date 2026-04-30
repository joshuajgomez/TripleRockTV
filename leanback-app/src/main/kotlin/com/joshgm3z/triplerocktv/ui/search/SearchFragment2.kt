package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
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

    private lateinit var rowsAdapter: ArrayObjectAdapter

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var streamPresenter: StreamPresenter

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
            }
        }
    }

    private fun initViews() {
        binding.keyboardView.listener = this
        val gridFragment = VerticalGridSupportFragment()
        gridFragment.gridPresenter = VerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_XSMALL,
            false
        ).apply {
            numberOfColumns = 5
        }
        rowsAdapter = ArrayObjectAdapter(streamPresenter)
        gridFragment.adapter = rowsAdapter
//        gridFragment.onItemViewClickedListener = clickListener
//        gridFragment.setOnItemViewSelectedListener(selectionListener)

        childFragmentManager.beginTransaction()
            .replace(binding.flStreamRowContainer.id, gridFragment)
            .commit()
    }

    override fun onTextChange(text: String) {
        binding.etInput.setText(text)
    }

    override fun onClick(item: String) {
        binding.etInput.setText(item)
    }
}