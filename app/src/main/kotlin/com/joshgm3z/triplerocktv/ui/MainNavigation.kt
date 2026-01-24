package com.joshgm3z.triplerocktv.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joshgm3z.triplerocktv.repository.data.MediaData
import com.joshgm3z.triplerocktv.ui.home.HomeScreen
import com.joshgm3z.triplerocktv.ui.loading.MediaLoadingScreen
import com.joshgm3z.triplerocktv.ui.login.LoginScreen
import com.joshgm3z.triplerocktv.ui.player.MediaInfoScreen
import com.joshgm3z.triplerocktv.ui.search.SearchScreen
import kotlinx.serialization.Serializable

val borderPaddingHorizontal = 20.dp
val borderPaddingVertical = 12.dp

@Serializable
object NavHome

@Serializable
data class NavMediaInfo(val id: String)

@Serializable
object NavMediaPlayer

@Serializable
object NavLogin

@Serializable
object NavLoading

@Serializable
object NavSearch

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavLoading,
        modifier = Modifier.padding(
            horizontal = borderPaddingHorizontal,
            vertical = borderPaddingVertical
        )
    ) {
        composable<NavHome> {
            HomeScreen(
                openMediaInfoScreen = {
                    navController.navigate(NavMediaInfo(it.id))
                },
                openSearchScreen = {
                    navController.navigate(NavSearch)
                }
            )
        }
        composable<NavMediaInfo> { backStackEntry ->
            MediaInfoScreen(
                mediaData = MediaData.sample(),
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavSearch> {
            SearchScreen(
                openMediaInfoScreen = {
                    navController.navigate(NavMediaInfo(it.id))
                },
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavLogin> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavLoading)
                }
            )
        }
        composable<NavLoading> {
            MediaLoadingScreen(
                onMediaLoaded = {
                    navController.navigate(NavHome)
                }
            )
        }
    }
}