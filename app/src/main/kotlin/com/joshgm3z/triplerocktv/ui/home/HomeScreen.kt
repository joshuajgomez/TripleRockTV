package com.joshgm3z.triplerocktv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.common.defaultAnimationSpec
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
    NavigationDrawer(drawerContent = {
        Column(modifier = Modifier.padding(10.dp)) {
            TopMenuDropDown(uiState.selectedTopbarItem) { viewModel.onTopbarItemUpdate(it) }
            Spacer(Modifier.size(10.dp))
            if (!uiState.categoryEntities.isEmpty())
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
        }
    }) {
        Crossfade(uiState, animationSpec = defaultAnimationSpec) {
            with(it) {
                when {
                    isLoading -> InfoBox(text = "Loading data", delayMs = 0)
                    !errorMessage.isNullOrEmpty() -> InfoBox("Error loading content")
                    streamEntities.isNotEmpty() -> Content(
                        onContentClick = { streamEntity -> openMediaInfoScreen(streamEntity) },
                        streamEntities = streamEntities,
                    )

                    else -> InfoBox("No data found")
                }
            }
        }
    }
}

@Composable
fun TopMenuDropDown(
    selectedTopbarItem: TopbarItem?,
    onSelectionChange: (TopbarItem) -> Unit
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.width(150.dp)
            ) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(selectedTopbarItem?.name ?: "Select")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TopbarItem.entries.forEach { item ->
                    DropdownMenuItem(
                        text = { androidx.compose.material3.Text(item.name) },
                        onClick = {
                            onSelectionChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

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