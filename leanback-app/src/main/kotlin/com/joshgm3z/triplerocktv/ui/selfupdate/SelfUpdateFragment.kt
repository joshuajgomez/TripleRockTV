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
import com.joshgm3z.triplerocktv.databinding.LayoutDialogBinding
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
        binding = LayoutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.tvTitle.text = it.title
                binding.tvSubtitle.text = it.subtitle
                binding.bvPositive.text = it.buttonText
                binding.bvPositive.isEnabled = it.enableButtons
                binding.bvNegative.isEnabled = it.enableButtons
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

fun String.getRunNumber() = "-run(\\d+)"
    .toRegex()
    .find(this)
    ?.groupValues?.get(1)
    ?.toInt() ?: 0