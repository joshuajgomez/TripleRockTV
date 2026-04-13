package com.joshgm3z.triplerocktv.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel
import com.joshgm3z.triplerocktv.databinding.FragmentLoginBinding
import com.joshgm3z.triplerocktv.util.orIfDebug
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewLoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels()

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
                binding.includeLoginForm.btnLogin.isEnabled = !it.loading
                binding.tvStatus.setVisible(it.loginSuccess || !it.errorMessage.isNullOrEmpty())

                binding.tvStatus.text = it.errorMessage ?: "Login successful"
                binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (it.loginSuccess) R.drawable.ic_check_scaled
                        else R.drawable.ic_error_orange
                    ),
                    null, null, null
                )
            }
        }
        lifecycleScope.launch {
            viewModel.qrCodeUiState.collectLatest {
                binding.includeQrcodeForm.ivQrcode.setImageBitmap(it)
            }
        }
        initListeners()
        initUi()
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