package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

enum class TopbarItem {
    Search,
    Movies,
    Series,
    LiveTv,
}

@Composable
fun TopBar() {
    var selected by remember { mutableStateOf(TopbarItem.Movies) }
    Row(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.TopBar),
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        TopbarItem.entries.forEach {
            TopBarOption(
                item = it,
                selected = selected == it,
            ) {
                selected = it
            }
        }
    }
}

@Composable
fun TopBarOption(
    item: TopbarItem,
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
            .clip(RoundedCornerShape(40.dp))
            .background(color = colorBg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable(true) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item == TopbarItem.Search) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = colorFg,
                modifier = Modifier.padding(end = 5.dp)
            )
        }
        Text(
            text = item.label(),
            color = colorFg,
            fontSize = 15.sp,
        )
    }
}

private fun TopbarItem.label() = when (this) {
    TopbarItem.LiveTv -> "Live TV"
    else -> name
}

@Composable
@TvPreview
private fun PreviewTopBar() {
    TripleRockTVTheme {
        TopBar()
    }
}