package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.SectionTitle
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.BuildConfig
import com.joshgm3z.triplerocktv.compose.theme.cardColor

data class SettingData(
    val navMainDestination: NavMainDestination?,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

data class SettingCategory(
    val title: String,
    val settings: List<SettingData>
)

val settingsList = listOf(
    SettingCategory(
        "Content", listOf(
            SettingData(
                NavMainDestination.MediaSync,
                "Media sync",
                "Sync your media content",
                Icons.Default.CloudSync
            ),
        )
    ),
    SettingCategory(
        "App", listOf(
            SettingData(
                NavMainDestination.AppUpdate(),
                "App update",
                "Check for updates",
                Icons.Default.SystemUpdate
            ),
            SettingData(
                null,
                "App version",
                BuildConfig.VERSION_NAME,
                Icons.Default.PermDeviceInformation
            ),
        )
    ),
    SettingCategory(
        "Account", listOf(
            SettingData(
                NavMainDestination.EditLogin,
                "Change login credentials",
                "Modify your IPTV login credentials",
                Icons.Default.Edit
            ),
            SettingData(
                NavMainDestination.Logout,
                "Logout",
                "Log out from app",
                Icons.AutoMirrored.Default.ExitToApp
            )
        )
    ),
)

@Composable
fun SettingsScreen(onSettingClick: (NavMainDestination) -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(
                horizontal = appHorizontalPadding,
                vertical = 25.dp
            )
    ) {
        Text(
            text = "Settings",
            color = colorScheme.onBackground,
            style = typography.headlineMedium,
            modifier = Modifier
        )
        settingsList.forEach {
            SectionTitle(it.title)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = cardColor())
            ) {
                it.settings.forEachIndexed { index, data ->
                    SettingsItem(data) {
                        if (data.navMainDestination != null)
                            onSettingClick(data.navMainDestination)
                    }
                    if (index < it.settings.size - 1) HorizontalDivider(
                        thickness = 2.dp,
                        color = colorScheme.background
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    settingData: SettingData,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(true) { onClick() }
            .padding(
                horizontal = appHorizontalPadding,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = settingData.icon,
            contentDescription = settingData.title,
            tint = colorScheme.onBackground,
            modifier = Modifier
                .size(40.dp)
                .background(
                    colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(9.dp)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Column {
            Text(
                text = settingData.title,
                color = colorScheme.onBackground,
                style = typography.titleMedium,
            )
            Text(
                text = settingData.subtitle,
                color = colorScheme.onBackground.copy(alpha = 0.5f),
                style = typography.bodyMedium,
            )
        }
    }
}

@DarkPreview
@Composable
private fun PreviewSettingsScreen() {
    DarkSurface {
        SettingsScreen()
    }
}
