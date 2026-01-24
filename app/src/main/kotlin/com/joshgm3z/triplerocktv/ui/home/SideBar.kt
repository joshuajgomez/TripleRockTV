package com.joshgm3z.triplerocktv.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Gray900
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.HomeViewModel
import kotlin.random.Random

@Composable
fun SideBar(
    modifier: Modifier = Modifier,
    selected: Int = 0,
    viewModel: HomeViewModel = hiltViewModel(),
    onSelection: (Int) -> Unit = {},
) {
    val uiState = viewModel.uiState.collectAsState().value
    if (uiState.categories.isNotEmpty())
        LaunchedEffect(Unit) {
            onSelection(uiState.categories.first().categoryId)
        }
    Log.i("SideBar", "SideBar: uiState=$uiState")
    LazyColumn(
        modifier = modifier
            .layoutId(HomeScreenLayoutId.SideBar)
            .width(170.dp)
            .fillMaxHeight()
            .background(color = Gray900),
    ) {
        items(uiState.categories) {
            SideBarItem(
                it,
                selected = selected == it.categoryId,
            ) { onSelection(it.categoryId) }
        }
    }
}

@Composable
fun SideBarItem(
    category: CategoryEntity,
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
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .onFocusChanged { focused = it.isFocused }
            .background(color = colorBg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable(
                enabled = true,
                interactionSource = null,
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = category.categoryName,
            color = colorFg,
            fontSize = 11.sp,
            lineHeight = 15.sp,
        )
        Text(
            text = category.count.toString(),
            color = colorFg.copy(alpha = 0.3f),
            fontSize = 10.sp
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