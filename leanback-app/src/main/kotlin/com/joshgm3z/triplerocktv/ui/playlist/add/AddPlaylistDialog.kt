package com.joshgm3z.triplerocktv.ui.playlist.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistType
import com.joshgm3z.triplerocktv.core.viewmodel.AddPlaylistUiState
import com.joshgm3z.triplerocktv.core.viewmodel.AddPlaylistViewModel
import com.joshgm3z.triplerocktv.databinding.LayoutAddPlaylistDialogBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPlaylistDialog : DialogFragment(R.layout.layout_add_playlist_dialog) {

    private lateinit var binding: LayoutAddPlaylistDialogBinding

    private val viewModel: AddPlaylistViewModel by viewModels()

    private val args: AddPlaylistDialogArgs by navArgs()

    val isXtream: Boolean
        get() = args.playlistType == PlaylistType.Xtream

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
        binding = LayoutAddPlaylistDialogBinding.bind(view)

        binding.etPlaylistUsername.setVisible(isXtream)
        binding.etPlaylistPassword.setVisible(isXtream)
        binding.tvTitle.text = if (isXtream) "Add Xtream playlist" else "Add M3u8 playlist"

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                binding.bvSignIn.text = it.buttonText
                when (it) {
                    is AddPlaylistUiState.Initial -> showInitialData(it)
                    is AddPlaylistUiState.Loading -> showLoading(it)
                    is AddPlaylistUiState.LoginSuccess -> showSuccess()
                    is AddPlaylistUiState.Error -> showError(it.message)
                }
            }
        }
        binding.bvSignIn.setOnClickListener {
            val serverUrl = binding.etServerUrl.text.toString()

            if (isXtream) {
                val username = binding.etPlaylistUsername.text.toString()
                val password = binding.etPlaylistPassword.text.toString()
                viewModel.verifyXtreamLogin(serverUrl, username, password)
            } else {
                viewModel.verifyM3u8Url(serverUrl)
            }
        }
    }

    private fun showError(message: String) {
        binding.bvSignIn.isEnabled = true
        binding.tvStatus.text = message
        binding.tvStatus.setVisible(true)
    }

    private fun showSuccess() {
        lifecycleScope.launch {
            delay(1000)
            findNavController().popBackStack()
        }
    }

    private fun showLoading(uiState: AddPlaylistUiState.Loading) {
        binding.bvSignIn.isEnabled = false
        binding.tvStatus.setVisible(false)
    }

    private fun showInitialData(uiState: AddPlaylistUiState.Initial) {
        binding.bvSignIn.isEnabled = true
        binding.etServerUrl.setText(uiState.webUrl)
        binding.etPlaylistUsername.setText(uiState.username)
        binding.etPlaylistPassword.setText(uiState.password)
    }

}