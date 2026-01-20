package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

val borderPaddingHorizontal = 20.dp
val borderPaddingVertical = 12.dp

@Composable
fun HomeScreen() {
    ConstraintLayout(
        constraintSet = getHomeScreenConstraints(),
        modifier = Modifier.padding(
            horizontal = borderPaddingHorizontal,
            vertical = borderPaddingVertical
        )
    ) {
        TopBar()
        SideBar()
        Content()
        MenuIcon()
    }
}

@Composable
fun MenuIcon(onMenuClick: () -> Unit = {}) {
    IconButton(
        onClick = { onMenuClick() },
        modifier = Modifier.layoutId(HomeScreenLayoutId.Menu)
    ) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = null
        )
    }
}

@Composable
@Preview
private fun PreviewHomeScreen() {
    TripleRockTVTheme {
        HomeScreen()
    }
}