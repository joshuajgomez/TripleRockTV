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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import kotlinx.coroutines.delay

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
    NavigationDrawer(
        drawerContent = NavigationDrawerContent(
            uiState = uiState,
            onTopbarItemUpdate = { viewModel.onTopbarItemUpdate(it) },
            onSelectedCategoryUpdate = { viewModel.onSelectedCategoryUpdate(it) },
            closeDrawer = { drawerState.setValue(DrawerValue.Closed) },
            focusRestorer = focusRestorer,
        ),
        content = NavigationContent(
            uiState = uiState,
            openMediaInfoScreen = openMediaInfoScreen,
        )
    )
}

@Composable
fun InfoBox(
    text: String,
    delayMs: Long = 0
) {
    var showText by remember { mutableStateOf(delayMs == 0L) }
    LaunchedEffect(delayMs) {
        if (delayMs > 0) {
            delay(delayMs)
            showText = true
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showText) Text(text = text, fontSize = 20.sp)
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