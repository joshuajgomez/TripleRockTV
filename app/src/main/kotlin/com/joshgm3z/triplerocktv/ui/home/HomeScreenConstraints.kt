package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

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

    constrain(topBar) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
    }
    constrain(sideBar) {
        top.linkTo(parent.top)
        start.linkTo(topBar.start)
    }
    constrain(menu) {
        top.linkTo(topBar.top)
        end.linkTo(parent.end)
    }
    constrain(content) {
        top.linkTo(parent.top, 50.dp)
        start.linkTo(parent.start, 30.dp)
    }
}

@Composable
@TvPreview
private fun PreviewHomeScreen() {
    TripleRockTVTheme {
        HomeScreen()
    }
}