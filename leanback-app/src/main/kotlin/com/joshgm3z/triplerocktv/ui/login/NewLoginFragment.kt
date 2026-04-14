package com.joshgm3z.triplerocktv.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.OnlineTyperViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentLoginBinding
import com.joshgm3z.triplerocktv.util.orIfDebug
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewLoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    private val onlineTyperViewModel: OnlineTyperViewModel by viewModels()

    private val defaultValueServerUrl = "http://".orIfDebug(Secrets.webUrl)
    private val defaultValueUsername = "".orIfDebug(Secrets.username)
    private val defaultValuePassword = "".orIfDebug(Secrets.password)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                binding.loadingOverlay.setVisible(it.loading)
                listOf(
                    binding.includeLoginForm.etServerUrl,
                    binding.includeLoginForm.etUsername,
                    binding.includeLoginForm.etPassword,
                    binding.includeLoginForm.btnLogin,
                ).forEach { view -> view.isEnabled = !it.loading }
                binding.includeLoginForm.btnLogin.isEnabled = !it.loading

                binding.tvStatus.setVisible(it.loginSuccess || !it.errorMessage.isNullOrEmpty())
                when {
                    it.loginSuccess -> {
                        binding.tvStatus.text = "Login successful"
                        binding.tvStatus.setDrawable(R.drawable.ic_check_scaled)
                        delay(2000)
                        findNavController().navigate(NewLoginFragmentDirections.toMediaLoading())
                    }

                    !it.errorMessage.isNullOrEmpty() -> {
                        binding.tvStatus.text = it.errorMessage
                        binding.tvStatus.setDrawable(R.drawable.ic_error_orange)
                    }
                }
            }
        }
        lifecycleScope.launch {
            onlineTyperViewModel.qrCodeBitmapState.collectLatest {
                if (it != null) binding.includeQrcodeForm.ivQrcode.setImageBitmap(it)
            }
        }
        lifecycleScope.launch {
            onlineTyperViewModel.inputTextFlow.collectLatest { inputText ->
                listOf(
                    binding.includeLoginForm.etServerUrl,
                    binding.includeLoginForm.etUsername,
                    binding.includeLoginForm.etPassword,
                ).firstOrNull { it.hasFocus() }?.setText(inputText)
            }
        }
        initListeners()
        initUi()
    }

    private fun TextView.setDrawable(res: Int) {
        setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), res),
            null, null, null
        )
    }

    private fun initUi() {
        binding.includeLoginForm.let {
            it.etServerUrl.setText(defaultValueServerUrl)
            it.etUsername.setText(defaultValueUsername)
            it.etPassword.setText(defaultValuePassword)
        }
    }

    private fun initListeners() {
        binding.includeLoginForm.btnLogin.setOnClickListener {
            binding.includeLoginForm.let {
                viewModel.onLoginClick(
                    it.etServerUrl.text.toString(),
                    it.etUsername.text.toString(),
                    it.etPassword.text.toString()
                )
            }
        }
    }
}