package com.joshgm3z.triplerocktv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.util.Logger

enum class FocusItem {
    SideBar,
    Content,
    TopMenu,
}

@Composable
fun getHomeViewModel(): IHomeViewModel = when {
    LocalInspectionMode.current -> FakeHomeViewModel()
    else -> hiltViewModel<HomeViewModel>()
}

@Composable
fun HomeScreen(
    openMediaInfoScreen: (StreamEntity) -> Unit = {},
    openSearchScreen: () -> Unit = {},
    defaultFocus: FocusItem = FocusItem.TopMenu,
    viewModel: IHomeViewModel = getHomeViewModel(),
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is HomeUiState.Loading -> InfoBox("Loading data")
        is HomeUiState.Error -> InfoBox("Error loading content")
        is HomeUiState.Ready -> HomeScreenContent(
            uiState = uiState,
            openMediaInfoScreen = { openMediaInfoScreen(it) },
            defaultFocus = defaultFocus,
            onTopBarItemChange = {
                viewModel.fetchCategories(it)
            }
        )

        else -> InfoBox("No data found")
    }
}

@Composable
fun InfoBox(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState.Ready,
    defaultFocus: FocusItem = FocusItem.TopMenu,
    openMediaInfoScreen: (StreamEntity) -> Unit,
    onTopBarItemChange: (TopbarItem) -> Unit,
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
            onContentClick = { openMediaInfoScreen(it) },
            focus = focus,
            uiState = uiState,
            setFocus = { focus = it }
        )

        SideBar(
            focusedCategory = focusedCategory,
            focus = focus,
            uiState = uiState,
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