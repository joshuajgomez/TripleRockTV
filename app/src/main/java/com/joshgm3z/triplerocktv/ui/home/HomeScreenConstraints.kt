package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.runtime.Composable
import androidx.constraintlayout.compose.ConstraintSet

enum class HomeScreenLayoutId {
    TopBar,
    SideBar,
    Menu,
    Content,
}

@Composable
fun getHomeScreenConstraints(): ConstraintSet = ConstraintSet {
    val topBar = createRefFor(HomeScreenLayoutId.TopBar)
    val sideBar = createRefFor(HomeScreenLayoutId.SideBar)
    val menu = createRefFor(HomeScreenLayoutId.Menu)
    val content = createRefFor(HomeScreenLayoutId.Content)

    constrain(sideBar) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
    }
    constrain(topBar) {
        top.linkTo(sideBar.top)
        start.linkTo(sideBar.end)
        end.linkTo(menu.start)
    }
    constrain(menu) {
        top.linkTo(topBar.top)
        end.linkTo(parent.end)
    }
    constrain(content) {
        top.linkTo(topBar.bottom)
        start.linkTo(sideBar.end)
    }
}