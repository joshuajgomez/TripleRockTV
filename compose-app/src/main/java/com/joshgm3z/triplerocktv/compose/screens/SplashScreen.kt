package com.joshgm3z.triplerocktv.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme
import com.joshgm3z.triplerocktv.core.viewmodel.DestinationState
import com.joshgm3z.triplerocktv.core.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateMain: (NavMainDestination) -> Unit,
) {
    SplashScreenContent()
    viewModel.navDirectionState.collectAsState().value?.let { state ->
        val route = when (state) {
            is DestinationState.AccessDisabled -> NavMainDestination.AccessDisabled(state.message)
            is DestinationState.AppUpdateNeeded -> NavMainDestination.AppUpdateNeeded(state.message)
            DestinationState.Home -> NavMainDestination.Home
            DestinationState.Updater -> NavMainDestination.MediaSync
            DestinationState.Login -> NavMainDestination.Login
            is DestinationState.Error -> TODO()
        }
        LaunchedEffect(route) {
//            delay(1000)
            navigateMain(route)
        }
    }
}

@Composable
fun SplashScreenContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        CircularProgressIndicator()
    }
}

@DarkPreview
@Composable
fun PreviewSplashScreen() {
    TripleRockTvTheme {
        SplashScreenContent()
    }
}