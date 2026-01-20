package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun Content(topbarItem: TopbarItem = TopbarItem.Movies) {
    FlowRow(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.Content)
            .padding(top = 30.dp, start = 10.dp, end = 10.dp),
        maxItemsInEachRow = 5,
    ) {
        if (topbarItem == TopbarItem.Search) {
            ContentPlaceholder("Search for movies")
        } else {
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
            ThumbnailCard(topbarItem)
        }
    }
}

@TvPreview
@Composable
fun ContentPlaceholder(text: String = "Fetching movies") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(700.dp)
            .height(400.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.movie),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.size(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(10.dp))
            Text(text)
        }
    }
}

@TvPreview
@Composable
private fun PreviewContent() {
    TripleRockTVTheme {
        Content()
    }
}