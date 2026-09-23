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
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.core.viewmodel.LoginUiState
import com.joshgm3z.triplerocktv.core.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class LoginFragment : GuidedStepSupportFragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    companion object {
        const val ID_SERVER_URL = 0L
        const val ID_USERNAME = 1L
        const val ID_PASSWORD = 2L
        const val ID_BUTTON = 3L
        const val ID_STATUS = 4L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            loginViewModel.uiState.collectLatest {
                when (it) {
                    is LoginUiState.Initial -> showInitialData(it)
                    is LoginUiState.Loading -> showLoading()
                    is LoginUiState.LoginSuccess -> showLoginSuccess()
                    is LoginUiState.Error -> showLoginFailed(it.message)
                }
            }
        }
    }

    fun showInitialData(uiState: LoginUiState.Initial) {
        showStatus(null)
        enableViews(true)

        fun setField(id: Long, text: String) {
            val action = findActionById(id)
            action == null && return
            action.title = text
            action.editTitle = text
            val position = findActionPositionById(action.id)
            if (position != -1) {
                notifyActionChanged(position)
            }
        }

        setField(ID_USERNAME, uiState.username)
        setField(ID_PASSWORD, uiState.password)
        setField(ID_SERVER_URL, uiState.webUrl)
    }


    private fun enableViews(enable: Boolean) {
        listOf(
            findActionById(ID_SERVER_URL),
            findActionById(ID_USERNAME),
            findActionById(ID_PASSWORD),
            findActionById(ID_BUTTON),
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
            findNavController().navigate(LoginFragmentDirections.toSplash())
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Sign in", // Title
            "Sign in with your IPTV credentials", // Description
            "", // Breadcrumb
            ContextCompat.getDrawable(requireContext(), R.drawable.logo_vd_vector) // Icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_SERVER_URL)
                .title("Server URL")
                .description("Enter the server URL")
                .editable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_USERNAME)
                .title("Username")
                .description("Enter your username")
                .editable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_PASSWORD)
                .title("Password")
                .description("Enter your password")
                .editable(true)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_BUTTON)
                .title("Sign in")
                .icon(R.drawable.ic_arrow_forward)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_STATUS)
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
        val action = findActionById(ID_STATUS) ?: return
        action.title = message ?: ""
        action.description = description ?: ""
        action.icon = if (icon == null) null
        else ContextCompat.getDrawable(requireContext(), icon)
        notifyActionChanged(findActionPositionById(ID_STATUS))
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == ID_BUTTON) {
            val serverUrl = findActionById(ID_SERVER_URL)?.editTitle?.trim().toString()
            val username = findActionById(ID_USERNAME)?.editTitle?.trim().toString()
            val password = findActionById(ID_PASSWORD)?.editTitle?.trim().toString()
            // Handle login logic here

            if (isInputValid()) loginViewModel.onLoginClick(serverUrl, username, password)
        }
    }

    private fun isInputValid(): Boolean {
        val serverUrlEt = findActionById(ID_SERVER_URL)?.editTitle?.trim()
        if (serverUrlEt.isNullOrEmpty() || serverUrlEt.toString() == "http://") {
            selectedActionPosition = findActionPositionById(ID_SERVER_URL)
            return false
        }
        if (findActionById(ID_USERNAME)?.editTitle?.trim().isNullOrEmpty()) {
            selectedActionPosition = findActionPositionById(ID_USERNAME)
            return false
        }
        if (findActionById(ID_PASSWORD)?.editTitle?.trim().isNullOrEmpty()) {
            selectedActionPosition = findActionPositionById(ID_PASSWORD)
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
        if (listOf(ID_SERVER_URL, ID_USERNAME, ID_PASSWORD).contains(action.id)) {
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
            ID_SERVER_URL -> ID_USERNAME
            ID_USERNAME -> ID_PASSWORD
            ID_PASSWORD -> ID_BUTTON
            else -> -1
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(ScreenName.Login)
    }
}