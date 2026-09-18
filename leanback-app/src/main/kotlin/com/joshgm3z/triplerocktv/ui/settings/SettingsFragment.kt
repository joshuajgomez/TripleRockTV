package com.joshgm3z.triplerocktv.ui.settings

import android.annotation.SuppressLint
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
import com.joshgm3z.triplerocktv.core.BuildConfig
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.ui.login.LoginActionsStylist
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.core.util.formatExpiryDate
import com.joshgm3z.triplerocktv.core.viewmodel.SettingsViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class SettingsFragment : GuidedStepSupportFragment() {

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    companion object {
        const val ID_CREDENTIAL = 0L
        const val ID_SERVER_URL = 10L
        const val ID_USERNAME = 11L
        const val ID_PASSWORD = 12L
        const val ID_LOGIN = 13L
        const val ID_STATUS = 14L

        const val ID_SIGN_OUT = 1L

        const val ID_ACCOUNT_EXPIRY = 2L
        const val ID_APP_VERSION = 3L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.credentialState.collectLatest {
                Logger.debug("credentialState = [${it}]")
                it.userInfo?.let { userInfo -> updateCredentials(userInfo) }
                when {
                    it.loading -> showLoading()
                    it.errorMessage != null -> showLoginFailed(it.errorMessage!!)
                    it.verificationSuccess -> showLoginSuccess()
                }
            }
        }
    }

    private fun updateCredentials(userInfo: UserInfo) {
        fun setText(id: Long, text: String) {
            val subAction = getSubAction(id)
            subAction.editTitle = text
            subAction.title = text
        }
        setText(ID_SERVER_URL, userInfo.webUrl)
        setText(ID_USERNAME, userInfo.username)
        setText(ID_PASSWORD, userInfo.password)
        findActionById(ID_CREDENTIAL)?.isEnabled = true
        notifyActionChanged(findActionPositionById(ID_CREDENTIAL))
        findActionById(ID_ACCOUNT_EXPIRY)?.description =
            "Valid till ${userInfo.expiryDate.formatExpiryDate()}"
        notifyActionChanged(findActionPositionById(ID_ACCOUNT_EXPIRY))
    }

    private fun enableViews(enable: Boolean) {
        listOf(
            ID_SERVER_URL,
            ID_USERNAME,
            ID_PASSWORD,
            ID_LOGIN,
        ).forEach { getSubAction(it).isEnabled = enable }
    }

    fun showLoading() {
        enableViews(false)
        showStatus(message = "Verifying")
    }

    fun showLoginFailed(message: String?) {
        enableViews(true)
        showStatus(
            message = "Verification failed",
            description = message,
            icon = R.drawable.ic_error_orange
        )
    }

    fun showLoginSuccess() {
        enableViews(true)
        showStatus("Verified", icon = R.drawable.ic_check_circle_green)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Settings", // Title
            "Customize your 3RockTV", // Description
            "", // Breadcrumb
            null // Icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_CREDENTIAL) // Main Action ID
                .title("Sign in credentials")
                .description("View and update credentials")
                .enabled(false)
                .subActions(getCredentialSubActions()) // Attach the sub-actions here
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_SIGN_OUT) // Main Action ID
                .title("Sign out")
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_ACCOUNT_EXPIRY) // Main Action ID
                .title("IPTV account validity")
                .description("Checking")
                .focusable(false)
                .infoOnly(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_APP_VERSION) // Main Action ID
                .title("App version")
                .description(BuildConfig.VERSION_NAME)
                .focusable(false)
                .infoOnly(true)
                .build()
        )
    }

    private fun getCredentialSubActions(): List<GuidedAction> {
        val subActions = mutableListOf<GuidedAction>()
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_SERVER_URL)
                .title("")
                .editTitle("")
                .description("Server URL")
                .editable(true)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_USERNAME)
                .title("")
                .editTitle("")
                .description("Username")
                .editable(true)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_PASSWORD)
                .title("")
                .editTitle("")
                .description("Password")
                .editable(true)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_LOGIN)
                .title("Update")
                .icon(R.drawable.ic_arrow_forward)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_STATUS)
                .title("")
                .focusable(false) // Prevents user from selecting it
                .infoOnly(true)   // Styles it as informational text
                .multilineDescription(true)
                .build()
        )
        return subActions
    }

    private fun getSubAction(id: Long) =
        findActionById(ID_CREDENTIAL)?.subActions?.first { it.id == id }!!

    @SuppressLint("NotifyDataSetChanged")
    private fun showStatus(
        message: String? = null,
        description: String? = null,
        icon: Int? = null
    ) {
        Logger.debug("message = [${message}], description = [${description}], icon = [${icon}]")
        val action = getSubAction(ID_STATUS)
        action.title = message ?: ""
        action.description = description ?: ""
        action.icon = if (icon == null) null
        else ContextCompat.getDrawable(requireContext(), icon)

        guidedActionsStylist.subActionsGridView?.adapter?.notifyDataSetChanged()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        Logger.debug("action = [${action.id}]")
        when (action.id) {
            ID_SIGN_OUT -> SettingsFragmentDirections.toSignOutDialog()
                .let { findNavController().navigate(it) }
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        Logger.debug("action = [${action.id}]")
        when (action.id) {
            ID_LOGIN -> {
                val subActions = findActionById(ID_CREDENTIAL)?.subActions
                fun getSubActionText(id: Long): String {
                    return subActions?.find { it.id == id }?.editTitle?.toString() ?: ""
                }

                val serverUrl = getSubActionText(ID_SERVER_URL)
                val username = getSubActionText(ID_USERNAME)
                val password = getSubActionText(ID_PASSWORD)
                // Handle login logic here

                if (isInputValid()) viewModel.verifyCredentials(serverUrl, username, password)
            }
        }
        return false
    }

    private fun isInputValid(): Boolean {
        fun getSubActionText(id: Long): String {
            return getSubAction(id).editTitle?.toString() ?: ""
        }

        val serverUrl = getSubActionText(ID_SERVER_URL)
        if (serverUrl.isEmpty() || serverUrl == "http://") {
            selectedActionPosition = findActionPositionById(ID_SERVER_URL)
            return false
        }
        if (getSubActionText(ID_USERNAME).isEmpty()) {
            selectedActionPosition = findActionPositionById(ID_USERNAME)
            return false
        }
        if (getSubActionText(ID_PASSWORD).isEmpty()) {
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
        if (action.id == ID_SERVER_URL || action.id == ID_USERNAME || action.id == ID_PASSWORD) {
            val userInput = action.editTitle.toString()
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
        return action.id
    }

    override fun onResume() {
        super.onResume()
        firebaseLogger.logScreenView(ScreenName.Settings)
    }
}