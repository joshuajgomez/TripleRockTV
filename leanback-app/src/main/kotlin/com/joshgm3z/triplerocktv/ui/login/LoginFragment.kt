package com.joshgm3z.triplerocktv.ui.login

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.util.orIfDebug
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class LoginFragment : GuidedStepSupportFragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        val idServerUrl = 0L
        val idUsername = 1L
        val idPassword = 2L
        val idLogin = 3L
        val idStatus = 4L
        val defaultValueServerUrl = "http://".orIfDebug(Secrets.webUrl)
        val defaultValueUsername = "".orIfDebug(Secrets.username)
        val defaultValuePassword = "".orIfDebug(Secrets.password)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            loginViewModel.uiState.collectLatest {
                when {
                    it.loading -> showLoading()
                    it.loginSuccess -> showLoginSuccess()
                    !it.errorMessage.isNullOrEmpty() -> showLoginFailed(it.errorMessage)
                }
            }
        }
        copyInputToTitle(findActionById(idServerUrl))
        copyInputToTitle(findActionById(idUsername))
        copyInputToTitle(findActionById(idPassword))
    }


    private fun enableViews(enable: Boolean) {
        listOf(
            findActionById(idServerUrl),
            findActionById(idUsername),
            findActionById(idPassword),
            findActionById(idLogin),
        ).forEach {
            if (it == null) return@forEach
            it.isEnabled = enable
            notifyActionChanged(findActionPositionById(it.id))
        }
    }

    fun showLoading() {
        showStatus(message = "Signing in")
        enableViews(false)
    }

    fun showLoginFailed(message: String?) {
        showStatus(
            message = "Sign in failed",
            description = message,
            icon = R.drawable.ic_error_orange
        )
        enableViews(true)
    }

    fun showLoginSuccess() {
        showStatus("Signed in", icon = R.drawable.ic_check_circle_green)
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(LoginFragmentDirections.toMediaLoading())
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Sign in", // Title
            "Sign in using your IPTV credentials", // Description
            "", // Breadcrumb
            ContextCompat.getDrawable(requireContext(), R.drawable.logo_vd_vector) // Icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idServerUrl)
                .title("Server URL")
                .editTitle(defaultValueServerUrl)
                .description("Enter the server URL")
                .editable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idUsername)
                .title("Username")
                .editTitle(defaultValueUsername)
                .description("Enter your username")
                .editable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idPassword)
                .title("Password")
                .editTitle(defaultValuePassword)
                .description("Enter your password")
                .editable(true)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idLogin)
                .title("Sign in")
                .icon(R.drawable.ic_arrow_forward)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idStatus)
                .title("")
                .focusable(false) // Prevents user from selecting it
                .infoOnly(true)   // Styles it as informational text
                .multilineDescription(true)
                .build()
        )
    }

    private fun showStatus(
        message: String? = null,
        description: String? = null,
        icon: Int? = null
    ) {
        val action = findActionById(idStatus) ?: return
        action.title = message ?: ""
        action.description = description ?: ""
        action.icon = if (icon == null) null
        else ContextCompat.getDrawable(requireContext(), icon)
        notifyActionChanged(findActionPositionById(idStatus))
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == idLogin) {
            val serverUrl = findActionById(idServerUrl)?.editTitle?.trim().toString()
            val username = findActionById(idUsername)?.editTitle?.trim().toString()
            val password = findActionById(idPassword)?.editTitle?.trim().toString()
            // Handle login logic here

            if (isInputValid()) loginViewModel.onLoginClick(serverUrl, username, password)
        }
    }

    private fun isInputValid(): Boolean {
        val serverUrlEt = findActionById(idServerUrl)?.editTitle?.trim()
        if (serverUrlEt.isNullOrEmpty() || serverUrlEt.toString() == "http://") {
            selectedActionPosition = findActionPositionById(idServerUrl)
            return false
        }
        if (findActionById(idUsername)?.editTitle?.trim().isNullOrEmpty()) {
            selectedActionPosition = findActionPositionById(idUsername)
            return false
        }
        if (findActionById(idPassword)?.editTitle?.trim().isNullOrEmpty()) {
            selectedActionPosition = findActionPositionById(idPassword)
            return false
        }

        return true
    }

    override fun onCreateActionsStylist(): GuidedActionsStylist {
        return LoginActionsStylist()
    }

    override fun onGuidedActionEditCanceled(action: GuidedAction) {
        super.onGuidedActionEditCanceled(action)
        copyInputToTitle(action)
    }

    private fun copyInputToTitle(action: GuidedAction?) {
        action ?: return
        if (listOf(idServerUrl, idUsername, idPassword).contains(action.id)) {
            val userInput = action.editTitle?.toString()
            if (userInput.isNullOrEmpty()) return
            action.title = userInput

            val position = findActionPositionById(action.id)
            if (position != -1) {
                notifyActionChanged(position)
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long {
        copyInputToTitle(action)
        // Return ACTION_NEXT to move to next field, or action.id to stay
        return when (action.id) {
            idServerUrl -> idUsername
            idUsername -> idPassword
            idPassword -> idLogin
            else -> -1
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseLogger.logScreenView(ScreenName.Login)
    }
}