package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

enum class TopbarItem {
    Search,
    Home,
    Movies,
    Series,
    LiveTv,
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    focusedTopBarItem: TopbarItem = TopbarItem.Movies,
    onFocusedTopBarItemChange: (TopbarItem) -> Unit = {},
) {
    val topBarModifier = modifier.height(50.dp)

    Row(
        modifier = topBarModifier
            .layoutId(HomeScreenLayoutId.TopBar)
            .fillMaxWidth()
            .padding(start = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.logo_3rocktv_cutout),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                TopbarItem.entries.forEach {
                    TopBarOption(
                        item = it,
                        focused = focusedTopBarItem == it,
                        selected = focusedTopBarItem == it,
                        onFocused = { onFocusedTopBarItemChange(it) },
                        onClick = { onItemClick() }
                    )
                }
            }
            MenuIcon()
        }

    }
}

@Composable
fun TopBarOption(
    item: TopbarItem,
    focused: Boolean = false,
    selected: Boolean = false,
    onFocused: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val colorFg = when {
        selected -> colorScheme.onBackground
        focused -> colorScheme.background
        else -> colorScheme.onBackground.copy(alpha = 0.6f)
    }
    val colorBg = when {
        selected -> colorScheme.background
        focused -> colorScheme.onBackground
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .onFocusChanged { if (it.isFocused) onFocused() }
            .clip(RoundedCornerShape(40.dp))
            .background(color = colorBg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clickable(
                enabled = true,
                interactionSource = null,
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item == TopbarItem.Search) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = colorFg,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(15.dp)
            )
        }
        Text(
            text = item.label(),
            color = colorFg,
            fontSize = 13.sp,
            fontWeight = if (focused) FontWeight.Bold else FontWeight.Normal
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

@Composable
@TvPreview
private fun PreviewTopBarFocused() {
    TripleRockTVTheme {
        TopBar()
    }
}