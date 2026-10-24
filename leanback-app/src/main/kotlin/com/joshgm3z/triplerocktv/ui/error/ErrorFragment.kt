package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.databinding.FragmentErrorBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ErrorFragment : Fragment(R.layout.fragment_error) {

    private val args: ErrorFragmentArgs by navArgs()

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentErrorBinding.bind(view)
        binding.tvTitle.text = args.title
        binding.tvMessage.text = args.message

        binding.bvDismiss.setOnClickListener {
            if (!args.goToSplashOnDismiss) findNavController().popBackStack()
            else findNavController().navigate(ErrorFragmentDirections.toSplash())
        }

        binding.bvDismiss.post {
            binding.bvDismiss.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(ScreenName.Error, mapOf("error_message" to args.message))
    }
}