package com.joshgm3z.triplerocktv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.login.ILoginViewModel
import com.joshgm3z.triplerocktv.ui.login.getLoginViewModel
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun SettingsScreen(
    viewModel: ILoginViewModel = getLoginViewModel(),
    onUserLoggedOut: () -> Unit = {},
) {
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        WideButton(
            enabled = !loading,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            loading -> "Signing out"
                            else -> "Sign out"
                        }
                    )
                    if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            },
            onClick = {
                viewModel.onLogoutClick {
                    onUserLoggedOut()
                }
            }
        )
    }
}

@TvPreview
@Composable
private fun PreviewSettingsScreen() {
    TripleRockTVTheme {
        SettingsScreen()
    }
}