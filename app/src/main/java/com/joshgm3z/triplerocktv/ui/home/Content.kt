package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun Content() {
    FlowRow(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.Content)
            .padding(top = 30.dp, start = 10.dp, end = 10.dp),
        maxItemsInEachRow = 5,
    ) {
        ThumbnailCard()
        ThumbnailCard()
        ThumbnailCard()
        ThumbnailCard()
        ThumbnailCard()
        ThumbnailCard()
        ThumbnailCard()
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