package com.joshgm3z.triplerocktv.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.databinding.FragmentSplashScreenBinding
import com.joshgm3z.triplerocktv.repository.impl.LocalDatastore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var binding: FragmentSplashScreenBinding? = null

    @Inject
    lateinit var localDatastore: LocalDatastore

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
        lifecycleScope.launch {
            delay(1000)
            val action = if (localDatastore.getUserInfo() == null)
                SplashScreenFragmentDirections.actionSplashToLogin()
            else
                SplashScreenFragmentDirections.actionSplashToBrowse()
            findNavController().navigate(action)
        }
        lifecycleScope.launch {
            delay(1200)
            binding?.progressBar?.visibility = View.VISIBLE
        }
    }
}