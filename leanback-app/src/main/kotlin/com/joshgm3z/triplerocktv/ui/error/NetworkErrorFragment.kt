package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.R
import androidx.leanback.app.ErrorSupportFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NetworkErrorFragment : ErrorSupportFragment() {

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.lb_ic_sad_cloud)
        message = "Internet not connected. Please check you internet and try again"
        buttonText = "Try again"

        buttonClickListener = View.OnClickListener {
            findNavController().navigate(NetworkErrorFragmentDirections.toSplash())
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(ScreenName.Error, mapOf("error_message" to "internet not connected"))
    }
}