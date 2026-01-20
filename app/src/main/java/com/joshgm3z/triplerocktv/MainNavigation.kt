package com.joshgm3z.triplerocktv

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.joshgm3z.triplerocktv.ui.home.HomeScreen
import com.joshgm3z.triplerocktv.ui.home.MediaItem
import com.joshgm3z.triplerocktv.ui.player.MediaInfoScreen
import com.joshgm3z.triplerocktv.ui.search.SearchScreen
import kotlinx.serialization.Serializable

val borderPaddingHorizontal = 20.dp
val borderPaddingVertical = 12.dp

@Serializable
private object Home

@Serializable
private data class NavMediaInfo(val mediaItem: MediaItem)

@Serializable
private object NavMediaPlayer

@Serializable
private object NavLogin

@Serializable
private object NavLoading

@Serializable
private object NavSearch

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                openMediaInfoScreen = {
                    navController.navigate(NavMediaInfo(it))
                },
                openSearchScreen = {
                    navController.navigate(NavSearch)
                }
            )
        }
        composable<NavMediaInfo> { backStackEntry ->
            val item: MediaItem = backStackEntry.toRoute()
            MediaInfoScreen(
                mediaItem = item,
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavSearch> {
            SearchScreen(
                openMediaInfoScreen = {
                    navController.navigate(NavMediaInfo(it))
                },
                goBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}