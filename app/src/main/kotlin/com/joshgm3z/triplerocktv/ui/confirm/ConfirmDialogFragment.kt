package com.joshgm3z.triplerocktv.ui.confirm

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.navigation.fragment.findNavController
import com.joshgm3z.triplerocktv.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ConfirmDialogFragment : GuidedStepSupportFragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    companion object{
        const val idYes = 1L
        const val idNo = 2L
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            "Sign out", // Title
            "Are you sure you want to sign out?", // Description
            "", // Breadcrumb
            null // Icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idYes)
                .title("Yes")
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(idNo)
                .title("No")
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            idYes -> {
                // Handle confirmation
                loginViewModel.onLogoutClick {
                    findNavController().navigate(ConfirmDialogFragmentDirections.toLogin())
                }
            }

            idNo -> {
                // Handle cancellation
                parentFragmentManager.popBackStack()
            }
        }
    }
}
