package com.joshgm3z.triplerocktv.ui.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.databinding.FragmentErrorBinding

class LoadingErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding

    private val args: LoadingErrorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = args.title
        binding.tvSubtitle.text = args.summary
        binding.btnBack.requestFocus()
        binding.btnBack.setOnClickListener {
            findNavController().navigate(LoadingErrorFragmentDirections.toSplash())
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseLogger.logScreenView(ScreenName.Error, mapOf("error_message" to args.title))
    }
}