package com.joshgm3z.triplerocktv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

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
    viewModel: IHomeViewModel = getHomeViewModel(),
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is HomeUiState.Loading -> InfoBox("Loading data")
        is HomeUiState.Error -> InfoBox("Error loading content")
        is HomeUiState.Ready -> HomeScreenContent(
            uiState = uiState,
            openMediaInfoScreen = { openMediaInfoScreen(it) },
            onTopBarItemChange = { viewModel.fetchCategories(it) },
            onSelectedCategoryEntityChange = { viewModel.fetchContent(it) },
            openSearchScreen = { openSearchScreen() }
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
    openMediaInfoScreen: (StreamEntity) -> Unit,
    openSearchScreen: () -> Unit,
    onTopBarItemChange: (TopbarItem) -> Unit,
    onSelectedCategoryEntityChange: (CategoryEntity) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    BackHandler(enabled = true) {
        when (drawerState.currentValue) {
            DrawerValue.Closed -> DrawerValue.Open
            else -> DrawerValue.Closed
        }.let { drawerState.setValue(it) }
    }

    ModalNavigationDrawer(drawerContent = {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
        ) {
            items(uiState.categoryEntities) { categoryEntity ->
                NavigationDrawerItem(
                    selected = uiState.selectedCategoryEntity == categoryEntity,
                    onClick = {
                        onSelectedCategoryEntityChange(categoryEntity)
                        drawerState.setValue(DrawerValue.Closed)
                    },
                    content = { Text(text = categoryEntity.categoryName) },
                    leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                )
            }
        }
    }) {
        Content(
            onContentClick = { openMediaInfoScreen(it) },
            uiState = uiState,
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