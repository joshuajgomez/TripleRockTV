package com.joshgm3z.triplerocktv.ui.login

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class GuidedLoginFragment : GuidedStepSupportFragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val idServerUrl = 0L
    private val idUsername = 1L
    private val idPassword = 2L
    private val idLogin = 3L

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
    }

    fun showLoading() {
        actions[idLogin.toInt()].description = ""
        actions[idLogin.toInt()].title = "Signing in"
        enableViews(false)
    }

    private fun enableViews(enable: Boolean) {
        actions.forEach {
            it.isEnabled = enable
            notifyActionChanged(it.id.toInt())
        }
    }

    fun showLoginFailed(message: String?) {
        actions[idLogin.toInt()].title = "Sign in"
        actions[idLogin.toInt()].description = message
        enableViews(true)
        actions = actions
    }

    fun showLoginSuccess() {
        actions[idLogin.toInt()].title = "Signed in"
        notifyActionChanged(idLogin.toInt())
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(R.id.action_login_to_mediaLoading)
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Sign in", // Title
            "Sign in using your IPTV credentials", // Description
            "", // Breadcrumb
            ContextCompat.getDrawable(context, R.drawable.logo_3rocktv_cutout) // Icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idServerUrl)
                .title("Server URL")
                .editTitle("http://")
                .description("Enter the server URL")
                .editable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idUsername)
                .title("Username")
                .editTitle("")
                .description("Enter your username")
                .editable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idPassword)
                .title("Password")
                .editTitle("")
                .description("Enter your password")
                .editable(true)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idLogin)
                .title("Login")
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == idLogin) {
            val serverUrl = actions[idServerUrl.toInt()].editTitle.toString()
            val username = actions[idUsername.toInt()].editTitle.toString()
            val password = actions[idPassword.toInt()].editTitle.toString()
            // Handle login logic here
            loginViewModel.onLoginClick(serverUrl, username, password)
        }
    }

    override fun onGuidedActionEditCanceled(action: GuidedAction) {
        super.onGuidedActionEditCanceled(action)
        copyInputToTitle(action)
    }

    private fun copyInputToTitle(action: GuidedAction) {
        if (action.id == idServerUrl || action.id == idUsername || action.id == idPassword) {
            val userInput = action.editTitle.toString()
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
}