package com.joshgm3z.triplerocktv.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.FragmentSettingsBinding
import com.joshgm3z.triplerocktv.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnUpdate.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_mediaLoading)
        }

        binding.btnSignOut.setOnClickListener {
            viewModel.onLogoutClick {
                lifecycleScope.launch {
                    delay(1000)
                    findNavController().navigate(R.id.action_settings_to_login)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.userInfo.collectLatest {
                it?.let { userInfo ->
                    binding.tvServerUrl.text = userInfo.webUrl
                    binding.tvUsername.text = userInfo.username
                    binding.tvExpiryDate.text = userInfo.expiryDate
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}