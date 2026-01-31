package com.joshgm3z.triplerocktv.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joshgm3z.triplerocktv.ui.home.HomeScreen
import com.joshgm3z.triplerocktv.ui.loading.MediaLoadingScreen
import com.joshgm3z.triplerocktv.ui.loading.SplashScreen
import com.joshgm3z.triplerocktv.ui.login.LoginScreen
import com.joshgm3z.triplerocktv.ui.player.MediaInfoScreen
import com.joshgm3z.triplerocktv.ui.search.SearchScreen
import com.joshgm3z.triplerocktv.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

val borderPaddingHorizontal = 20.dp
val borderPaddingVertical = 12.dp

@Serializable
object NavHome

@Serializable
object NavSplash

@Serializable
data class NavMediaInfo(val streamId: Int)

@Serializable
object NavMediaPlayer

@Serializable
object NavLogin

@Serializable
object NavLoading

@Serializable
object NavSettings

@Serializable
object NavSearch

@Composable
fun MainNavigation(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    viewModel.isLoggedIn.collectAsState().value?.let {
        navController.navigate(if (it) NavHome else NavLogin)
    }

    NavHost(
        navController = navController,
        startDestination = NavSplash,
        modifier = Modifier.padding(
            horizontal = borderPaddingHorizontal,
            vertical = borderPaddingVertical
        )
    ) {
        composable<NavSplash> {
            SplashScreen()
        }
        composable<NavHome> {
            HomeScreen(
                openMediaInfoScreen = {
                    navController.navigate(NavMediaInfo(it.streamId))
                },
                openSearchScreen = {
                    navController.navigate(NavSearch)
                },
                openSettingsScreen = {
                    navController.navigate(NavSettings)
                }
            )
        }
        composable<NavMediaInfo> {
            MediaInfoScreen(
                goBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavSearch> {
            SearchScreen(
                openMediaInfoScreen = {
                    navController.navigate(NavMediaInfo(it.streamId))
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
        composable<NavSettings> {
            SettingsScreen(
                onUserLoggedOut = {
                    navController.navigate(NavLogin) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                gotoMediaLoadingScreen = {
                    navController.navigate(NavLoading)
                },
                goBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}