package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun SideBar() {
    LazyColumn(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.SideBar),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(5) {
            SideBarItem("English 4K($it/00)")
        }
    }
}

@Composable
fun SideBarItem(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    var focused by remember { mutableStateOf(false) }
    val colorFg = if (selected) colorScheme.background
    else colorScheme.onBackground.copy(alpha = 0.6f)
    val colorBg = when {
        selected -> colorScheme.onBackground.copy(alpha = 0.7f)
        focused -> colorScheme.onBackground.copy(alpha = 0.1f)
        else -> colorScheme.background
    }
    Row(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .clip(RoundedCornerShape(5.dp))
            .background(color = colorBg)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .clickable(true) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = colorFg,
        )
    }
}

@TvPreview
@Composable
private fun PreviewSideBar() {
    TripleRockTVTheme {
        SideBar()
    }
}