package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.ErrorSupportFragment
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R

class AppUpdateInfoFragment : ErrorSupportFragment() {

    val args: ErrorFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_circle_down)
        message = args.message
        buttonText = "Exit app"

        buttonClickListener = View.OnClickListener {
            requireActivity().finishAffinity()
        }
    }
}