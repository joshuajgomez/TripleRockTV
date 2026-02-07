package com.joshgm3z.triplerocktv.ui.settings

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
import com.joshgm3z.triplerocktv.ui.login.LoginActionsStylist
import com.joshgm3z.triplerocktv.ui.login.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class GuidedSettingsFragment : GuidedStepSupportFragment() {

    private val viewModel: SettingsViewModel by viewModels()

    companion object {
        val idCredential = 0L
        val idServerUrl = 10L
        val idUsername = 11L
        val idPassword = 12L
        val idLogin = 13L
        val idStatus = 14L

        val idBlur = 1L
        val idSignout = 2L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.credentialState.collectLatest {
                it.userInfo?.let { userInfo -> updateCredentials(userInfo) }
                updateBlurSettings(it.isBlurSettingEnabled)
            }
        }
    }

    private fun updateBlurSettings(enabled: Boolean) {
        findActionById(idBlur)?.let {
            it.isChecked = enabled
            notifyActionChanged(findActionPositionById(idBlur))
        }
    }

    private fun updateCredentials(userInfo: UserInfo) {
        fun setText(id: Long, text: String) {
            findActionById(idCredential)?.let {
                try {
                    val subAction = it.subActions.first { it.id == id }
                    subAction.editTitle = text
                    subAction.title = text
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        setText(idServerUrl, userInfo.webUrl)
        setText(idUsername, userInfo.username)
        setText(idPassword, userInfo.password)
        findActionById(idCredential)?.isEnabled = true
        notifyActionChanged(findActionPositionById(idCredential))
    }

    private fun enableViews(enable: Boolean) {
        listOf(
            actions[idServerUrl.toInt()],
            actions[idUsername.toInt()],
            actions[idPassword.toInt()],
            actions[idLogin.toInt()],
        ).forEach {
            it.isEnabled = enable
            notifyActionChanged(it.id.toInt())
        }
    }

    fun showLoading() {
        showStatus(message = "Verifying")
        enableViews(false)
    }

    fun showLoginFailed(message: String?) {
        showStatus(
            message = "Verification failed",
            description = message,
            icon = R.drawable.ic_error_orange
        )
        enableViews(true)
    }

    fun showLoginSuccess() {
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
                .id(idCredential) // Main Action ID
                .title("Sign in credentials")
                .description("View and update credentials")
                .enabled(false)
                .subActions(getCredentialSubActions()) // Attach the sub-actions here
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idBlur) // Main Action ID
                .title("Enable blur effect")
                .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                .checked(false)
                .description("Uncheck this if app seems slow due to blur")
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idSignout) // Main Action ID
                .title("Sign out")
                .build()
        )
    }

    private fun getCredentialSubActions(): List<GuidedAction> {
        val subActions = mutableListOf<GuidedAction>()
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(idServerUrl)
                .title("")
                .editTitle("")
                .description("Server URL")
                .editable(true)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(idUsername)
                .title("")
                .editTitle("")
                .description("Username")
                .editable(true)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(idPassword)
                .title("")
                .editTitle("")
                .description("Password")
                .editable(true)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(idLogin)
                .title("Update")
                .icon(R.drawable.ic_arrow_forward)
                .build()
        )
        subActions.add(
            GuidedAction.Builder(requireContext())
                .id(idStatus)
                .title("")
                .focusable(false) // Prevents user from selecting it
                .infoOnly(true)   // Styles it as informational text
                .multilineDescription(true)
                .build()
        )
        return subActions
    }

    private fun showStatus(
        message: String? = null,
        description: String? = null,
        icon: Int? = null
    ) {
        val action = actions[idStatus.toInt()]
        action.title = message ?: ""
        action.description = description ?: ""
        action.icon = if (icon == null) null
        else ContextCompat.getDrawable(requireContext(), icon)
        notifyActionChanged(idStatus.toInt())
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            idLogin -> {
                val serverUrl = actions[idServerUrl.toInt()].editTitle.toString()
                val username = actions[idUsername.toInt()].editTitle.toString()
                val password = actions[idPassword.toInt()].editTitle.toString()
                // Handle login logic here

                if (isInputValid()) viewModel.verifyCredentials(serverUrl, username, password)
            }

            idSignout -> {
                findNavController().navigate(
                    GuidedSettingsFragmentDirections
                        .actionSettingsToConfirmSignOutDialog()
                )
            }

            idBlur -> {
                viewModel.setBlurSetting(action.isChecked)
            }
        }
    }

    private fun isInputValid(): Boolean {
        val serverUrlEt = actions[idServerUrl.toInt()].editTitle
        if (serverUrlEt.isNullOrEmpty() || serverUrlEt.toString() == "http://") {
            selectedActionPosition = idServerUrl.toInt()
            return false
        }
        if (actions[idUsername.toInt()].editTitle.isNullOrEmpty()) {
            selectedActionPosition = idUsername.toInt()
            return false
        }
        if (actions[idPassword.toInt()].editTitle.isNullOrEmpty()) {
            selectedActionPosition = idPassword.toInt()
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
        if (action.id == idServerUrl || action.id == idUsername || action.id == idPassword) {
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
}