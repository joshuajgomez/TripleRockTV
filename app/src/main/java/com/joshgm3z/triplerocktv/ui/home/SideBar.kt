package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import kotlin.random.Random

@Composable
fun SideBar(selected: Int = 0) {
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .layoutId(HomeScreenLayoutId.SideBar)
            .width(190.dp)
            .background(color = colorScheme.background),
    ) {
        items(5) {
            SideBarItem(
                text = "English 4K($it/00)",
                selected = selected == it
            )
        }
    }
}

@Composable
fun SideBarItem(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    var focused by remember { mutableStateOf(Random.nextBoolean()) }
    val colorFg = when {
        selected -> colorScheme.primary
        else -> colorScheme.onBackground.copy(alpha = 0.6f)
    }
    val colorBg = when {
        selected -> colorScheme.primaryContainer
        focused -> colorScheme.primaryContainer.copy(alpha = 0.2f)
        else -> colorScheme.background
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .onFocusChanged { focused = it.isFocused }
            .background(color = colorBg)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .clickable(true) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = colorFg,
            fontSize = 13.sp
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