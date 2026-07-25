package com.joshgm3z.triplerocktv.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.joshgm3z.triplerocktv.compose.screens.settings.AccessDisabledScreen
import com.joshgm3z.triplerocktv.compose.screens.settings.AppUpdateNeededScreen
import com.joshgm3z.triplerocktv.compose.screens.settings.AppUpdateScreen
import com.joshgm3z.triplerocktv.compose.screens.settings.EditLoginScreen
import com.joshgm3z.triplerocktv.compose.screens.LoginScreen
import com.joshgm3z.triplerocktv.compose.screens.settings.MediaSyncScreen
import com.joshgm3z.triplerocktv.compose.screens.PlaybackScreen
import com.joshgm3z.triplerocktv.compose.screens.SearchScreen
import com.joshgm3z.triplerocktv.compose.screens.SplashScreen
import com.joshgm3z.triplerocktv.compose.screens.StreamDetailsScreen
import com.joshgm3z.triplerocktv.compose.screens.browse.CategoryBrowseScreen
import com.joshgm3z.triplerocktv.compose.screens.common.ErrorDialog
import com.joshgm3z.triplerocktv.compose.screens.home.HomeScreen
import com.joshgm3z.triplerocktv.compose.screens.settings.LogoutScreen
import com.joshgm3z.triplerocktv.core.repository.StreamType
import kotlinx.serialization.Serializable

open class NavMainDestination {
    @Serializable
    object Login : NavMainDestination()

    @Serializable
    object Splash : NavMainDestination()

    @Serializable
    object EditLogin : NavMainDestination()

    @Serializable
    object MediaSync : NavMainDestination()

    @Serializable
    object Logout : NavMainDestination()

    @Serializable
    class AppUpdate(val isComposeApp: Boolean = true) : NavMainDestination()

    @Serializable
    class AppUpdateNeeded(val message: String) : NavMainDestination()

    @Serializable
    class AccessDisabled(val message: String) : NavMainDestination()

    @Serializable
    object Home : NavMainDestination()

    @Serializable
    object Search : NavMainDestination()

    @Serializable
    class Details(val streamId: Int, val streamType: StreamType) : NavMainDestination()

    @Serializable
    class Error(val message: String, val summary: String? = null) : NavMainDestination()

    @Serializable
    class StreamCatalogue(
        val categoryId: Int = 727,
        val selectedStreamId: Int? = null,
        val streamType: StreamType = StreamType.LiveTV
    ) : NavMainDestination()

    @Serializable
    class Playback(
        val streamId: Int,
        val streamType: StreamType,
        val resume: Boolean = false,
        val seriesId: Int? = null
    ) : NavMainDestination()
}

@Composable
fun TvNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavMainDestination.Splash) {
        composable<NavMainDestination.Login> {
            LoginScreen(onLoginSuccess = {
                navController.navigate(NavMainDestination.Home)
            })
        }
        composable<NavMainDestination.Splash> {
            SplashScreen {
                navController.navigate(it)
            }
        }
        composable<NavMainDestination.AccessDisabled> {
            AccessDisabledScreen(
                message = it.toRoute<NavMainDestination.AccessDisabled>().message,
                onExitClicked = {}
            )
        }
        composable<NavMainDestination.AppUpdateNeeded> {
            AppUpdateNeededScreen(
                message = it.toRoute<NavMainDestination.AppUpdateNeeded>().message,
                onExitClicked = {},
                navigateToAppUpdate = {
                    navController.navigate(NavMainDestination.AppUpdate())
                }
            )
        }
        composable<NavMainDestination.Logout> {
            LogoutScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutComplete = {
                    navController.navigate(NavMainDestination.Login) {
                        popUpTo<NavMainDestination.Splash> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<NavMainDestination.EditLogin> {
            EditLoginScreen(onBackClick = {
                navController.popBackStack()
            })
        }

        composable<NavMainDestination.MediaSync> {
            MediaSyncScreen(onSyncComplete = {
                /*navController.navigate(NavMainDestination.Home)*/
            }, onBackPress = {
                navController.popBackStack()
            })
        }
        composable<NavMainDestination.Home> {
            HomeScreen {
                navController.navigate(it)
            }
        }
        composable<NavMainDestination.Details> {
            StreamDetailsScreen(
                navigateMain = {
                    navController.navigate(it)
                }, onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavMainDestination.AppUpdate> {
            AppUpdateScreen {
                navController.popBackStack()
            }
        }

        composable<NavMainDestination.Playback> {
            PlaybackScreen(navController = navController)
        }

        composable<NavMainDestination.Search> {
            SearchScreen(
                navigate = { navController.navigate(it) },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavMainDestination.StreamCatalogue> { it ->
            val selectedStreamId by it.savedStateHandle
                .getStateFlow<Int?>("selectedStreamId", null)
                .collectAsState()
            CategoryBrowseScreen(
                selectedStreamId = selectedStreamId,
                navigateMain = {
                    navController.navigate(it)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavMainDestination.Error> {
            val message = it.toRoute<NavMainDestination.Error>().message
            val summary = it.toRoute<NavMainDestination.Error>().summary
            ErrorDialog(message = message, summary = summary) {
                navController.popBackStack()
            }
        }
    }
}
