package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun Content() {
    FlowRow(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.Content)
            .background(color = colorScheme.errorContainer)
    ) {
        ThumbnailCard()
    }
}

@Preview
@Composable
private fun PreviewContent() {
    TripleRockTVTheme {
        Content()
    }
}