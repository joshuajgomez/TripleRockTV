package com.joshgm3z.triplerocktv.compose.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.browse.BrowseScreen
import com.joshgm3z.triplerocktv.compose.screens.settings.SettingsScreen
import com.joshgm3z.triplerocktv.core.repository.StreamType
import kotlinx.serialization.Serializable

open class NavHomeDestination {
    @Serializable
    class Vod(val streamType: StreamType = StreamType.VideoOnDemand) : NavHomeDestination()

    @Serializable
    class Series(val streamType: StreamType = StreamType.Series) : NavHomeDestination()

    @Serializable
    class LiveTv(val streamType: StreamType = StreamType.LiveTV) : NavHomeDestination()

    @Serializable
    object Settings : NavHomeDestination()
}

@Composable
fun TvHomeNavHost(
    modifier: Modifier = Modifier,
    destination: NavHomeDestination = NavHomeDestination.Vod(),
    navigateMain: (NavMainDestination) -> Unit = {},
    navHomeController: NavHostController
) {
    NavHost(
        navController = navHomeController,
        startDestination = destination,
        modifier = modifier
    ) {
        composable<NavHomeDestination.Vod> {
            BrowseScreen {
                navigateMain(it)
            }
        }
        composable<NavHomeDestination.Series> {
            BrowseScreen {
                navigateMain(it)
            }
        }
        composable<NavHomeDestination.LiveTv> {
            BrowseScreen {
                navigateMain(it)
            }
        }
        composable<NavHomeDestination.Settings> {
            SettingsScreen {
                navigateMain(it)
            }
        }
    }
}