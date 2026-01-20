package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.TopBar)
            .background(color = colorScheme.onPrimary)
    ) {
        TopBarOption("Search")
        TopBarOption("Movies")
        TopBarOption("Series")
        TopBarOption("Live")
    }
}

@Composable
fun TopBarOption(title: String) {
    Text(title)
}

@Preview
@Composable
private fun PreviewTopBar() {
    TripleRockTVTheme {
        TopBar()
    }
}