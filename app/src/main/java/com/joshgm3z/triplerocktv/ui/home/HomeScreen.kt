package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.joshgm3z.triplerocktv.repository.data.MediaData
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun HomeScreen(
    openMediaInfoScreen: (MediaData) -> Unit = {},
    openSearchScreen: () -> Unit = {},
) {
    var selectedTopbarItem: TopbarItem by remember { mutableStateOf(TopbarItem.Movies) }

    ConstraintLayout(
        constraintSet = getHomeScreenConstraints(),
        modifier = Modifier.padding(10.dp)
    ) {
        TopBar {
            if (it == TopbarItem.Search) openSearchScreen()
            else selectedTopbarItem = it
        }
        SideBar()
        Content(selectedTopbarItem) {
            openMediaInfoScreen(it)
        }
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
@TvPreview
private fun PreviewHomeScreen() {
    TripleRockTVTheme {
        HomeScreen()
    }
}