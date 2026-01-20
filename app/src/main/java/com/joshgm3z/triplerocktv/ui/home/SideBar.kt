package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun SideBar() {
    LazyColumn(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.SideBar)
            .background(color = colorScheme.error)
    ) {
        items(5) {
            Text(text = "Item $it")
        }
    }
}

@Preview
@Composable
private fun PreviewSideBar() {
    TripleRockTVTheme {
        SideBar()
    }
}