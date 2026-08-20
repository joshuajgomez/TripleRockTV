package com.joshgm3z.triplerocktv.ui.selfupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.viewmodel.SelfUpdateViewModel
import com.joshgm3z.triplerocktv.databinding.LayoutDialogBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelfUpdateDialog : DialogFragment() {

    private lateinit var binding: LayoutDialogBinding

    private val viewModel: SelfUpdateViewModel by viewModels()

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
        binding.bvNegative.setVisible(true)
        lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.tvTitle.text = it.title
                binding.tvSubtitle.text = it.subtitle
                binding.bvPositive.text = it.buttonAction.text
                binding.bvPositive.isEnabled = it.enableButtons
                binding.bvNegative.isEnabled = it.enableButtons

                binding.bvPositive.setVisible(it.enableButtons)
                binding.bvNegative.setVisible(it.enableButtons)
                binding.progressBar.setVisible(!it.enableButtons)

                if (it.enableButtons) {
                    binding.bvPositive.post {
                        binding.bvPositive.requestFocus()
                    }
                }
            }
        }

        binding.bvPositive.setOnClickListener {
            viewModel.onButtonClick()
        }

        binding.bvNegative.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
