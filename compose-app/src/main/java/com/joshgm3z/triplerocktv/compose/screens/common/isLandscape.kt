package com.joshgm3z.triplerocktv.compose.screens.common

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isLandscape() = LocalConfiguration.current.orientation ==
        Configuration.ORIENTATION_LANDSCAPE