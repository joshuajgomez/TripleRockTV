package com.joshgm3z.triplerocktv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.joshgm3z.triplerocktv.ui.common.BackButton
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.login.ILoginViewModel
import com.joshgm3z.triplerocktv.ui.login.UserInfo
import com.joshgm3z.triplerocktv.ui.login.getLoginViewModel
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingsScreen(
    viewModel: ILoginViewModel = getLoginViewModel(),
    onUserLoggedOut: () -> Unit = {},
    gotoMediaLoadingScreen: () -> Unit = {},
    goBack: () -> Unit = {},
) {
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        BackButton(onClick = { goBack() })
        Spacer(Modifier.size(20.dp))
        UserInfo(viewModel.userInfo)
        Spacer(Modifier.size(20.dp))
        CustomWideButton(
            title = "Update content",
            icon = Icons.Default.KeyboardArrowDown,
            onClick = { gotoMediaLoadingScreen() }
        )
        CustomWideButton(
            enabled = !loading,
            title = "Sign out",
            icon = Icons.Default.Close,
            onClick = {
                viewModel.onLogoutClick {
                    onUserLoggedOut()
                }
            }
        )
    }
}

@Composable
fun UserInfo(userInfoFlow: StateFlow<UserInfo?>) {
    userInfoFlow.collectAsState().value?.let {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            InfoItem("Server URL", it.webUrl)
            InfoItem("Username", it.username)
            InfoItem("Password", it.password)
            InfoItem("Expiry date", it.expiryDate)
        }
    }
}

@Composable
fun InfoItem(label: String, text: String) {
    Row {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(10.dp))
        Text(text)
    }
}

@Composable
fun CustomWideButton(
    enabled: Boolean = true,
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    WideButton(
        enabled = enabled,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!enabled)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else icon?.let {
                    Icon(it, contentDescription = null)
                }
                Text(
                    text = when {
                        !enabled -> "Signing out"
                        else -> title
                    }
                )
            }
        },
        onClick = {
            onClick()
        }
    )
}

@TvPreview
@Composable
private fun PreviewSettingsScreen() {
    TripleRockTVTheme {
        SettingsScreen()
    }
}