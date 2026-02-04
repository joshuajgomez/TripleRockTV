package com.joshgm3z.triplerocktv.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.FragmentLoginBinding
import com.joshgm3z.triplerocktv.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.ui.common.LoginButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun showLoading() {
        binding.loginButton.state = LoginButton.State.SigningIn
        enableViews(false)
    }

    fun showLoginFailed(message: String?) {
        binding.loginButton.state = LoginButton.State.Initial
        binding.errorCard.text = message
        enableViews(true)
    }

    fun showLoginSuccess() {
        binding.loginButton.state = LoginButton.State.Success
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(R.id.action_login_to_mediaLoading)
        }
    }

    fun enableViews(enable: Boolean) = listOf(
        binding.etServerUrl,
        binding.etUsername,
        binding.etPassword
    ).forEach {
        it.isEnabled = enable
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etServerUrl.setText(Secrets.webUrl)
        binding.etUsername.setText(Secrets.username)
        binding.etPassword.setText(Secrets.password)

        lifecycleScope.launch {
            loginViewModel.uiState.collectLatest {
                when {
                    it.loading -> showLoading()
                    it.loginSuccess -> showLoginSuccess()
                    !it.errorMessage.isNullOrEmpty() -> showLoginFailed(it.errorMessage)
                }
            }
        }


        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (isInputValid()) loginViewModel.onLoginClick(
                    binding.etServerUrl.text.toString().trim(),
                    binding.etUsername.text.toString().trim(),
                    binding.etPassword.text.toString().trim()
                )
            }
            true
        }

        binding.loginButton.setOnClickListener {
            if (isInputValid()) loginViewModel.onLoginClick(
                binding.etServerUrl.text.toString().trim(),
                binding.etUsername.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }
    }

    private fun isInputValid(): Boolean {
        var isValid = true

        if (binding.etServerUrl.text.isNullOrEmpty()) {
            binding.etServerUrl.error = "Server URL is required"
            isValid = false
        }
        if (binding.etUsername.text.isNullOrEmpty()) {
            binding.etUsername.error = "Username is required"
            isValid = false
        }
        if (binding.etPassword.text.isNullOrEmpty()) {
            binding.etPassword.error = "Password is required"
            isValid = false
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}