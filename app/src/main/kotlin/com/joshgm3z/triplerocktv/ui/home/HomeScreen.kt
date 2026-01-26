package com.joshgm3z.triplerocktv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

enum class FocusItem {
    SideBar,
    Content,
    TopMenu,
}

@Composable
fun HomeScreen(
    openMediaInfoScreen: (StreamEntity) -> Unit = {},
    openSearchScreen: () -> Unit = {},
    defaultFocus: FocusItem = FocusItem.TopMenu
) {
    var focusedTopbarItem: TopbarItem by remember { mutableStateOf(TopbarItem.Home) }
    var focusedCategory: Int by remember { mutableIntStateOf(0) }

    var focus by remember { mutableStateOf(defaultFocus) }

    BackHandler(enabled = focus != FocusItem.TopMenu) {
        focus = when (focus) {
            FocusItem.Content -> FocusItem.SideBar
            else -> FocusItem.TopMenu
        }
    }

    ConstraintLayout(
        constraintSet = getHomeScreenConstraints(),
    ) {

        Content(
            categoryId = focusedCategory,
            onContentClick = { openMediaInfoScreen(it) },
            focus = focus,
            setFocus = { focus = it }
        )

        SideBar(
            focusedCategory = focusedCategory,
            focus = focus,
            onCategoryFocus = { focusedCategory = it },
            onClick = { focus = FocusItem.Content },
            setFocus = { focus = it }
        )

        TopBar(
            setFocus = { focus = it },
            focus = focus,
            onItemClick = { focus = FocusItem.SideBar },
            focusedTopBarItem = focusedTopbarItem,
            onFocusedTopBarItemChange = { focusedTopbarItem = it }
        )
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