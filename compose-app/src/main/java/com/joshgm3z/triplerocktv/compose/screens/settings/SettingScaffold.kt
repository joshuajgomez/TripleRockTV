package com.joshgm3z.triplerocktv.compose.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.screens.browse.Header
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.appBottomPadding
import com.joshgm3z.triplerocktv.compose.screens.common.appHorizontalPadding

@Composable
fun SettingScaffold(
    title: String,
    innerPadding: Boolean = true,
    onBackClick: () -> Unit = {},
    applyBottomPadding: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = if (applyBottomPadding) appBottomPadding() else 0.dp)
    ) {
        Header(title, onBackClick)
        Spacer(Modifier.size(10.dp))
        Column(
            modifier = if (!innerPadding) Modifier
            else Modifier.padding(horizontal = appHorizontalPadding)
        ) {
            content()
        }
    }
}

@DarkPreview
@Composable
private fun PreviewSettingScaffold() {
    DarkSurface {
        SettingScaffold(
            title = "Settings",
            onBackClick = {}
        ) {
            Text(
                "Content start",
                color = colorScheme.onBackground
            )
            Spacer(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
            Text(
                "Content end",
                color = colorScheme.onBackground
            )
        }
    }
}