package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.R
import androidx.leanback.app.ErrorSupportFragment
import androidx.navigation.fragment.navArgs

class ErrorFragment : ErrorSupportFragment() {

    val args: ErrorFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.lb_ic_sad_cloud)
        message = args.message
        buttonText = "Dismiss"

        buttonClickListener = View.OnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}