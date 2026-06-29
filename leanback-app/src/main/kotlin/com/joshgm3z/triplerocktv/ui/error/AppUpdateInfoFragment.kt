package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.LayoutDialogBinding
import com.joshgm3z.triplerocktv.util.setVisible

class AppUpdateInfoFragment : DialogFragment() {
    private lateinit var binding: LayoutDialogBinding
    private val args: AppUpdateInfoFragmentArgs by navArgs()

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
        binding = LayoutDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvTitle.text = "App update needed"
        binding.tvSubtitle.text = args.message

        binding.bvPositive.text = "Check updates"
        binding.bvPositive.setOnClickListener {
            findNavController().navigate(AppUpdateInfoFragmentDirections.toSelfUpdateDialog())
        }

        binding.bvNegative.setVisible(true)
        binding.bvNegative.text = "Exit app"
        binding.bvNegative.setOnClickListener {
            requireActivity().finishAffinity()
        }
    }
}