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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
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
    focus: FocusItem = FocusItem.TopMenu,
    onItemClick: (TopbarItem) -> Unit = {},
    setFocus: (FocusItem) -> Unit = {},
    focusedTopBarItem: TopbarItem,
    onFocusedTopBarItemChange: (TopbarItem) -> Unit = {},
) {
    val topMenuFocusRequester = remember { FocusRequester() }
    LaunchedEffect(focus) {
        if (focus == FocusItem.TopMenu) topMenuFocusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .focusRequester(topMenuFocusRequester)
            .onFocusChanged { if (it.hasFocus) setFocus(FocusItem.TopMenu) }
            .layoutId(HomeScreenLayoutId.TopBar)
            .height(40.dp)
            .fillMaxWidth()
            .background(color = colorScheme.background)
            .padding(start = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo_3rocktv_cutout),
            contentDescription = null
        )
        TopbarItem.entries.forEach {
            TopBarOption(
                item = it,
                focused = focusedTopBarItem == it,
                onFocused = { onFocusedTopBarItemChange(it) },
                onClick = { onItemClick(it) }
            )
        }
    }
}

@Composable
fun TopBarOption(
    item: TopbarItem,
    focused: Boolean = false,
    onFocused: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val colorFg = if (focused) colorScheme.background
    else colorScheme.onBackground.copy(alpha = 0.6f)
    val colorBg = when {
        focused -> colorScheme.onBackground
        else -> colorScheme.background
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
            fontSize = if (focused) 17.sp else 15.sp,
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
//        TopBar()
    }
}