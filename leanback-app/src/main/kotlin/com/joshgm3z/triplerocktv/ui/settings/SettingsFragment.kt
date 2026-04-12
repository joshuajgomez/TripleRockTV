package com.joshgm3z.triplerocktv.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.BuildConfig
import com.joshgm3z.triplerocktv.core.util.FirebaseLogger
import com.joshgm3z.triplerocktv.ui.login.LoginActionsStylist
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.ScreenName
import com.joshgm3z.triplerocktv.core.viewmodel.OnlineTyperViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.SettingsViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SettingsFragment : GuidedStepSupportFragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private val onlineTyperViewModel: OnlineTyperViewModel by activityViewModels()

    companion object {
        val idCredential = 0L
        val idServerUrl = 10L
        val idUsername = 11L
        val idPassword = 12L
        val idLogin = 13L
        val idStatus = 14L

        val idBlur = 1L
        val idSignout = 2L
        val idAppVersion = 3L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.credentialState.collectLatest {
                updateBlurSettings(it.isBlurSettingEnabled)
                Logger.debug("credentialState = [${it}]")
                it.userInfo?.let { userInfo -> updateCredentials(userInfo) }
                when {
                    it.loading -> showLoading()
                    it.errorMessage != null -> showLoginFailed(it.errorMessage!!)
                    it.verificationSuccess -> showLoginSuccess()
                }
            }
        }
        lifecycleScope.launch {
            onlineTyperViewModel.inputTextFlow.collectLatest { inputText ->
                Logger.debug("inputTextFlow = [${inputText}]")
                val findSelectedAction = findSelectedAction()
                Logger.debug("findSelectedAction = [${findSelectedAction}]")
                findSelectedAction?.let {
                    it.editTitle = inputText
                    // 2. Find the ViewHolder and update the text view directly to avoid "notifyItemChanged" collapse
                    val subGridView = guidedActionsStylist.subActionsGridView
                    if (subGridView != null) {
                        val subActions = findActionById(idCredential)?.subActions
                        val index = subActions?.indexOf(it) ?: -1

                        if (index != -1) {
                            val vh = subGridView.findViewHolderForAdapterPosition(index) as? GuidedActionsStylist.ViewHolder
                            if (vh != null) {
                                // This updates the UI immediately without triggering the adapter lifecycle that causes collapse
                                vh.titleView?.text = inputText
                            } else {
                                // Fallback if view isn't bound, though unlikely for a focused item
                                subGridView.adapter?.notifyItemChanged(index)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun findSelectedAction(): GuidedAction? {
        // 1. Check if a sub-action is focused (this is usually what's visible when typing)
        val subActions = findActionById(idCredential)?.subActions
        if (subActions != null) {
            val subGridView = guidedActionsStylist.subActionsGridView
            if (subGridView != null && subGridView.hasFocus()) {
                val focusedView = subGridView.focusedChild
                val position = subGridView.getChildAdapterPosition(focusedView)
                if (position != -1 && position < subActions.size) {
                    return subActions[position]
                }
            }
        }

        // 2. Fallback to main actions list
        val mainGridView = guidedActionsStylist.actionsGridView
        if (mainGridView != null && mainGridView.hasFocus()) {
            val focusedView = mainGridView.focusedChild
            val position = mainGridView.getChildAdapterPosition(focusedView)
            if (position != -1 && position < actions.size) {
                return actions[position]
            }
        }

        return null
    }

    private fun updateBlurSettings(enabled: Boolean) {
        findActionById(idBlur)?.let {
            it.isChecked = enabled
            notifyActionChanged(findActionPositionById(idBlur))
        }
    }

    private fun updateCredentials(userInfo: UserInfo) {
        fun setText(id: Long, text: String) {
            val subAction = getSubAction(id)
            subAction.editTitle = text
            subAction.title = text
        }
        setText(idServerUrl, userInfo.webUrl)
        setText(idUsername, userInfo.username)
        setText(idPassword, userInfo.password)
        findActionById(idCredential)?.isEnabled = true
        notifyActionChanged(findActionPositionById(idCredential))
    }

    private fun enableViews(enable: Boolean) {
        listOf(
            idServerUrl,
            idUsername,
            idPassword,
            idLogin,
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
                .id(idCredential) // Main Action ID
                .title("Sign in credentials")
                .description("View and update credentials")
                .enabled(false)
                .subActions(getCredentialSubActions()) // Attach the sub-actions here
                .build()
        )
        /*actions.add(
            GuidedAction.Builder(requireContext())
                .id(idBlur) // Main Action ID
                .title("Enable blur effect")
                .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                .checked(false)
                .description("Uncheck this if app seems slow due to blur")
                .build()
        )*/
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idSignout) // Main Action ID
                .title("Sign out")
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idAppVersion) // Main Action ID
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

    private fun getSubAction(id: Long) =
        findActionById(idCredential)?.subActions?.first { it.id == id }!!

    @SuppressLint("NotifyDataSetChanged")
    private fun showStatus(
        message: String? = null,
        description: String? = null,
        icon: Int? = null
    ) {
        Logger.debug("message = [${message}], description = [${description}], icon = [${icon}]")
        val action = getSubAction(idStatus)
        action.title = message ?: ""
        action.description = description ?: ""
        action.icon = if (icon == null) null
        else ContextCompat.getDrawable(requireContext(), icon)

        guidedActionsStylist.subActionsGridView?.adapter?.notifyDataSetChanged()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        Logger.debug("action = [${action.id}]")
        when (action.id) {
            idSignout -> SettingsFragmentDirections.toConfirmSignOutDialog()
                .let { findNavController().navigate(it) }

            idBlur -> viewModel.setBlurSetting(action.isChecked)
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        Logger.debug("action = [${action.id}]")
        when (action.id) {
            idLogin -> {
                val subActions = findActionById(idCredential)?.subActions
                fun getSubActionText(id: Long): String {
                    return subActions?.find { it.id == id }?.editTitle?.toString() ?: ""
                }

                val serverUrl = getSubActionText(idServerUrl)
                val username = getSubActionText(idUsername)
                val password = getSubActionText(idPassword)
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

        val serverUrl = getSubActionText(idServerUrl)
        if (serverUrl.isEmpty() || serverUrl == "http://") {
            selectedActionPosition = findActionPositionById(idServerUrl)
            return false
        }
        if (getSubActionText(idUsername).isEmpty()) {
            selectedActionPosition = findActionPositionById(idUsername)
            return false
        }
        if (getSubActionText(idPassword).isEmpty()) {
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

    override fun onResume() {
        super.onResume()
        FirebaseLogger.logScreenView(ScreenName.Settings)
    }
}