package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.ErrorSupportFragment
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R

class AccessDisabledFragment : ErrorSupportFragment() {

    val args: AccessDisabledFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)
        message = "Access restricted: ${args.message}"
        buttonText = "Exit app"

        buttonClickListener = View.OnClickListener {
            requireActivity().finishAffinity()
        }
    }
}