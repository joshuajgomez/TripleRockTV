package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme

@Composable
fun DarkSurface(
    content: @Composable () -> Unit,
) {
    TripleRockTvTheme {
        Surface(color = colorScheme.background) {
            content()
        }
    }
}