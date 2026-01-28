package com.joshgm3z.triplerocktv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusGroup
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val focusRestorer = remember { FocusRequester() }
    BackHandler(enabled = true) {
        when (drawerState.currentValue) {
            DrawerValue.Closed -> DrawerValue.Open
            else -> DrawerValue.Closed
        }.let { drawerState.setValue(it) }
    }

    val uiState = viewModel.uiState.collectAsState().value
    NavigationDrawer(drawerContent = {
        if (uiState.categoryEntities.isEmpty()) return@NavigationDrawer
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .focusGroup()
                .focusRestorer(focusRestorer)
        ) {
            items(uiState.categoryEntities) { categoryEntity ->
                NavigationDrawerItem(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) viewModel.onSelectedCategoryUpdate(categoryEntity)
                    },
                    selected = uiState.selectedCategoryEntity == categoryEntity,
                    onClick = { drawerState.setValue(DrawerValue.Closed) },
                    content = { Text(text = categoryEntity.categoryName) },
                    leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                )
            }
        }
    }) {
        when {
            uiState.isLoading -> InfoBox("Loading data")
            !uiState.errorMessage.isNullOrEmpty() -> InfoBox("Error loading content")
            uiState.streamEntities.isNotEmpty() -> Content(
                onContentClick = { openMediaInfoScreen(it) },
                streamEntities = uiState.streamEntities,
            )

            else -> InfoBox("No data found")
        }
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