package com.joshgm3z.triplerocktv.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.LayoutDialogBinding
import com.joshgm3z.triplerocktv.util.setVisible

class DownloadCancelDialog : DialogFragment() {
    private lateinit var binding: LayoutDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            requireContext().resources.getDimensionPixelSize(R.dimen.popup_width),
            requireContext().resources.getDimensionPixelSize(R.dimen.popup_height)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bvNegative.setVisible(true)
        binding.bvPositive.setOnClickListener {
            findNavController().navigate(DownloadCancelDialogDirections.toSplash())
        }

        binding.bvNegative.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}