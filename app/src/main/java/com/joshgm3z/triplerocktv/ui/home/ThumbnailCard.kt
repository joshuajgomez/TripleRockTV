package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun ThumbnailCard() {
    Text("Movie name")
}

@Preview
@Composable
private fun PreviewThumbnailCard() {
    TripleRockTVTheme {
        ThumbnailCard()
    }
}