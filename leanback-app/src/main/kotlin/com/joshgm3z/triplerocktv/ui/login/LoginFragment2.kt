package com.joshgm3z.triplerocktv.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentLoginBinding
import com.joshgm3z.triplerocktv.ui.login.LoginFragment.Companion.idPassword
import com.joshgm3z.triplerocktv.ui.login.LoginFragment.Companion.idServerUrl
import com.joshgm3z.triplerocktv.ui.login.LoginFragment.Companion.idUsername
import com.joshgm3z.triplerocktv.util.orIfDebug
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment2 : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        binding.etServerUrl.setText("http://".orIfDebug(Secrets.webUrl))
        binding.etUsername.setText("".orIfDebug(Secrets.username))
        binding.etPassword.setText("".orIfDebug(Secrets.password))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                listOf(
                    binding.etServerUrl,
                    binding.etPassword,
                    binding.etUsername,
                ).forEach {
                    it.isEnabled = !uiState.loading
                }
                binding.btnLogin.loading = uiState.loading

                if (uiState.loginSuccess) {
                    val toUpdater = LoginFragment2Directions.toUpdater().apply {
                        autoUpdateAndExit = true
                    }
                    findNavController().navigate(toUpdater)
                }
                binding.tvErrorStatus.text = uiState.errorMessage
                binding.tvErrorStatus.setVisible(!uiState.errorMessage.isNullOrEmpty())
            }
        }
        binding.btnLogin.setOnClickListener {
            if (isInputValid()) viewModel.onLoginClick(
                binding.etServerUrl.text.toString(),
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }

    private fun isInputValid(): Boolean {
        if (binding.etServerUrl.text.toString().isEmpty()
            || binding.etServerUrl.text.toString() == "http://"
        ) {
            binding.etServerUrl.error = "Server URL cannot be empty"
            return false
        }
        if (binding.etUsername.text.toString().isEmpty()) {
            binding.etUsername.error = "Username cannot be empty"
            return false
        }
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPassword.error = "Password cannot be empty"
            return false
        }
        return true
    }
}