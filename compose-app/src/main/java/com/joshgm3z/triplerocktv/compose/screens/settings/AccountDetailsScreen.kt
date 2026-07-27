package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.SectionTitle
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.retrofit.Secrets
import com.joshgm3z.triplerocktv.core.util.formatExpiryDate
import com.joshgm3z.triplerocktv.core.util.orIfDebug
import com.joshgm3z.triplerocktv.core.viewmodel.CredentialUiState
import com.joshgm3z.triplerocktv.core.viewmodel.SettingsViewModel
import com.joshgm3z.triplerocktv.core.viewmodel.UserInfo
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AccountDetailsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.credentialState.collectAsState()
    AccountDetailsScreenContent(
        uiState = uiState,
        onSubmitClicked = { serverUrl, username, password ->
            viewModel.verifyCredentials(
                serverUrl,
                username,
                password
            )
        }, onBackClick = {
            onBackClick()
        })
}

@Composable
private fun AccountDetailsScreenContent(
    uiState: CredentialUiState,
    onSubmitClicked: (
        serverUrl: String,
        username: String,
        password: String
    ) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    val status = when {
        uiState.verificationSuccess -> "Login details updated"
        uiState.errorMessage != null -> uiState.errorMessage
        else -> null
    }
    var serverUrl by remember { mutableStateOf("https://".orIfDebug(Secrets.webUrl)) }
    var username by remember { mutableStateOf("".orIfDebug(Secrets.username)) }
    var password by remember { mutableStateOf("".orIfDebug(Secrets.password)) }

    SettingScaffold(
        title = "Account details",
        innerPadding = false,
        onBackClick = onBackClick
    ) {
        SectionTitle("Account expiry")
        Column(
            modifier = Modifier
                .padding(horizontal = appHorizontalPadding)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(color = cardColor())
                .padding(10.dp)
        ) {
            Text(
                text = "Valid till ${uiState.userInfo?.expiryDate.formatExpiryDate()}",
                color = textColor()
            )
        }

        SectionTitle("Login details")
        Column(
            modifier = Modifier
                .padding(horizontal = appHorizontalPadding)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(color = cardColor())
                .padding(10.dp)
        ) {
            OutlinedTextField(
                enabled = !uiState.loading,
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                enabled = !uiState.loading,
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()

            )

            OutlinedTextField(
                enabled = !uiState.loading,
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()

            )
            AnimatedVisibility(status != null) {
                Text(status ?: "", modifier = Modifier.padding(top = 5.dp, start = 3.dp))
            }

            Spacer(modifier = Modifier.size(15.dp))
            PrimaryButton(
                loading = uiState.loading,
                onClick = {
                    onSubmitClicked(
                        serverUrl,
                        username,
                        password
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                text = "Update"
            )
        }
    }
}

@DarkPreview
@Composable
private fun PreviewAccountDetailsScreen() {
    DarkSurface {
        AccountDetailsScreenContent(
            CredentialUiState(
                userInfo = UserInfo(
                    expiryDate = "1800266814",
                    username = "jj",
                    password = "jj",
                    webUrl = "https://www.google.com",
                    lastContentUpdate = "",
                    sessionId = ""
                )
            )
        )
    }
}