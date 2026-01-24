package com.joshgm3z.triplerocktv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
) {
    var selectedTopbarItem: TopbarItem by remember { mutableStateOf(TopbarItem.Movies) }
    var selectedCategory: Int by remember { mutableIntStateOf(0) }

    val sidebarFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    val topMenuFocusRequester = remember { FocusRequester() }
    var focus by remember { mutableStateOf(FocusItem.Content) }

    BackHandler(enabled = true) {
        when (focus) {
            FocusItem.Content -> sidebarFocusRequester.requestFocus()
            else -> topMenuFocusRequester.requestFocus()
        }
    }

    ConstraintLayout(
        constraintSet = getHomeScreenConstraints(),
    ) {
        TopBar(
            modifier = Modifier
                .focusRequester(topMenuFocusRequester)
                .onFocusChanged { if (it.hasFocus) focus = FocusItem.TopMenu }
        ) {
            if (it == TopbarItem.Search) openSearchScreen()
            else selectedTopbarItem = it
        }
        SideBar(
            modifier = Modifier
                .focusRequester(sidebarFocusRequester)
                .onFocusChanged { if (it.hasFocus) focus = FocusItem.SideBar },
            selected = selectedCategory
        ) {
            selectedCategory = it
        }
        Content(
            modifier = Modifier
                .focusRequester(contentFocusRequester)
                .onFocusChanged { if (it.hasFocus) focus = FocusItem.Content },
            categoryId = selectedCategory
        ) {
            openMediaInfoScreen(it)
        }
        MenuIcon()

        LaunchedEffect(Unit) {
            contentFocusRequester.requestFocus()
        }
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