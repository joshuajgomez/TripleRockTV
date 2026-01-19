package com.joshgm3z.triplerocktv.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun HomeScreen() {
    Text("Home Screen")
}

@Composable
@Preview
private fun PreviewHomeScreen() {
    TripleRockTVTheme {
        HomeScreen()
    }
}