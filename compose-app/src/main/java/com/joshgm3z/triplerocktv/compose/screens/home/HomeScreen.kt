package com.joshgm3z.triplerocktv.compose.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme

sealed class BottomItem(
    val route: NavHomeDestination,
    val label: String,
    val icon: ImageVector
) {
    object Vod : BottomItem(NavHomeDestination.Vod(), "Vod", Icons.Default.Movie)
    object Series : BottomItem(NavHomeDestination.Series(), "Series", Icons.Default.Tv)
    object LiveTv : BottomItem(NavHomeDestination.LiveTv(), "Live", Icons.Default.LiveTv)
    object Settings : BottomItem(NavHomeDestination.Settings, "Settings", Icons.Default.Settings)
}

@Composable
fun HomeScreen(navigateMain: (NavMainDestination) -> Unit = {}) {
    val navHomeController = rememberNavController()
    val navBackStackEntry by navHomeController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        content = { paddingValues ->
            TvHomeNavHost(
                Modifier.padding(paddingValues),
                navigateMain = navigateMain,
                navHomeController = navHomeController,
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomItem.Vod,
                    BottomItem.Series,
                    BottomItem.LiveTv,
                    BottomItem.Settings
                )

                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true

                    NavigationBarItem(
                        selected = isSelected,
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        onClick = {
                            navHomeController.navigate(item.route) {
                                popUpTo(navHomeController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                NavigationBarItem(
                    selected = false,
                    label = { Text("Search") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    onClick = {
                        navigateMain(NavMainDestination.Search)
                    }
                )
            }
        }
    )
}

@DarkPreview
@Composable
private fun PreviewHomeScreen() {
    TripleRockTvTheme {
        HomeScreen()
    }
}