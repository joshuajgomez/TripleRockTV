package com.joshgm3z.triplerocktv.ui.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.MediaSyncService
import com.joshgm3z.triplerocktv.core.viewmodel.LogoutViewModel
import com.joshgm3z.triplerocktv.databinding.LayoutDialogBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SignOutDialog : DialogFragment() {

    private lateinit var binding: LayoutDialogBinding

    private val viewModel: LogoutViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.popup_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.popup_height)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvTitle.text = "Sign out"
        binding.tvSubtitle.text = "Are you sure you want to sign out?"
        binding.bvPositive.text = "Sign out"
        binding.bvNegative.text = "Cancel"
        binding.bvNegative.setVisible(true)
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.progressBar.setVisible(it)
                binding.bvPositive.setVisible(!it)
                binding.bvNegative.setVisible(!it)
                if (it) {
                    binding.tvTitle.text = "Signing out"
                    binding.tvSubtitle.text = ""
                }
            }
        }

        binding.bvPositive.setOnClickListener {
            viewModel.onLogoutClick {
                MediaSyncService.stop(requireContext())
                findNavController().navigate(SignOutDialogDirections.toSplash())
            }
        }

        binding.bvNegative.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
