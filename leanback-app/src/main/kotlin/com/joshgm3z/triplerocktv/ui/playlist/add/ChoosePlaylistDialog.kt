package com.joshgm3z.triplerocktv.ui.playlist.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.viewmodel.AddPlaylistUiState
import com.joshgm3z.triplerocktv.core.viewmodel.AddPlaylistViewModel
import com.joshgm3z.triplerocktv.databinding.LayoutAddPlaylistDialogBinding
import com.joshgm3z.triplerocktv.databinding.LayoutChoosePlaylistDialogBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChoosePlaylistDialog : DialogFragment(R.layout.layout_choose_playlist_dialog) {

    private lateinit var binding: LayoutChoosePlaylistDialogBinding

    override fun onStart() {
        super.onStart()
        val resources = requireContext().resources
        dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.add_playlist_popup_width),
            resources.getDimensionPixelSize(R.dimen.add_playlist_popup_height)
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LayoutChoosePlaylistDialogBinding.bind(view)
        binding.bvXtream.setOnClickListener {
            ChoosePlaylistDialogDirections.toAddPlaylistDialog().apply {
                isXtream = true
                findNavController().navigate(this)
            }
        }
        binding.bvM3u8.setOnClickListener {
            ChoosePlaylistDialogDirections.toAddPlaylistDialog().apply {
                isXtream = false
                findNavController().navigate(this)
            }
        }
    }
}
