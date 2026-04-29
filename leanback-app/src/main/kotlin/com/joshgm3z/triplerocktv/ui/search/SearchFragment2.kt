package com.joshgm3z.triplerocktv.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import com.joshgm3z.triplerocktv.databinding.FragmentSearchBinding
import com.joshgm3z.triplerocktv.ui.streamcatalogue.StreamPresenter
import javax.inject.Inject

class SearchFragment2 : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    lateinit var rowsAdapter: ArrayObjectAdapter

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

    private fun initViews() {
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
}