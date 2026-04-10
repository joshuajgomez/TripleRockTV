package com.joshgm3z.triplerocktv.ui.splash

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.joshgm3z.triplerocktv.databinding.FragmentSplashScreenBinding
import com.joshgm3z.triplerocktv.util.getBackgroundColor
import com.joshgm3z.triplerocktv.core.viewmodel.DestinationState
import com.joshgm3z.triplerocktv.core.viewmodel.SplashViewModel
import com.joshgm3z.triplerocktv.util.setBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var binding: FragmentSplashScreenBinding? = null

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().setBackground(requireContext().getBackgroundColor())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(
            inflater,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ivLogo?.drawable.let {
            if (it is Animatable) it.start()
        }
        lifecycleScope.launch {
            delay(1000)
            viewModel.navDirectionState.collectLatest {
                if (it is DestinationState.Home) {
                    Firebase.analytics.setUserId(it.userName)
                    Firebase.crashlytics.setUserId(it.userName ?: "Unknown user")
                }
                when (it) {
                    DestinationState.Login -> SplashScreenFragmentDirections.toLogin()
                    DestinationState.Loading -> SplashScreenFragmentDirections.toLoading()
                    is DestinationState.AccessDisabled -> SplashScreenFragmentDirections.toAccessDisabled(it.message)
                    is DestinationState.AppUpdateNeeded -> SplashScreenFragmentDirections.toAppUpdateInfo(it.message)
                    is DestinationState.Error -> SplashScreenFragmentDirections.toError(it.message)
                    is DestinationState.Home -> SplashScreenFragmentDirections.toHome()
                    else -> return@collectLatest
                }.let { destination ->
                    findNavController().navigate(destination)
                }
            }
        }
    }
}