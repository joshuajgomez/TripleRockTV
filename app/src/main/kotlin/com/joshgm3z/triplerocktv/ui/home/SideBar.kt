package com.joshgm3z.triplerocktv.ui.home

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.FakeHomeViewModel
import com.joshgm3z.triplerocktv.viewmodel.HomeViewModel
import com.joshgm3z.triplerocktv.viewmodel.IHomeViewModel

@Composable
fun getHomeViewModel(): IHomeViewModel = when {
    LocalInspectionMode.current -> FakeHomeViewModel()
    else -> hiltViewModel<HomeViewModel>()
}

@Composable
fun SideBar(
    modifier: Modifier = Modifier,
    viewModel: IHomeViewModel = getHomeViewModel(),
    focusedCategory: Int = 0,
    onCategoryFocus: (Int) -> Unit = {},
    onClick: () -> Unit = {},
    focus: FocusItem = FocusItem.SideBar,
    setFocus: (FocusItem) -> Unit = {},
) {
    val sidebarFocusRequester = remember { FocusRequester() }

    LaunchedEffect(focus) {
        if (focus == FocusItem.SideBar) sidebarFocusRequester.requestFocus()
    }

    val uiState = viewModel.uiState.collectAsState().value
    if (uiState.categories.isNotEmpty())
        LaunchedEffect(Unit) {
            onCategoryFocus(uiState.categories.first().categoryId)
        }

    val verticalGradient = Brush.horizontalGradient(
        colors = listOf(Color.Black, Color.Black, Color.Transparent)
    )
    LazyColumn(
        modifier = modifier
            .focusRequester(sidebarFocusRequester)
            .onFocusChanged {
                if (it.hasFocus) setFocus(FocusItem.SideBar)
            }
            .layoutId(HomeScreenLayoutId.SideBar)
            .width(if (focus == FocusItem.SideBar) 250.dp else 20.dp)
            .fillMaxHeight()
            .background(brush = verticalGradient),
    ) {
        items(uiState.categories) {
            SideBarItem(
                category = it,
                focused = focusedCategory == it.categoryId,
                onCategoryFocus = { onCategoryFocus(it.categoryId) },
                onClick = { onClick() },
            )
        }
    }
}

@Composable
fun SideBarItem(
    category: CategoryEntity,
    focused: Boolean,
    onCategoryFocus: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val colorFg = when {
        focused -> colorScheme.onBackground
        else -> colorScheme.onBackground.copy(alpha = 0.6f)
    }
    val fontWeight = when {
        focused -> FontWeight.Bold
        else -> FontWeight.Normal
    }
    val fontSize = when {
        focused -> 13.sp
        else -> 11.sp
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .onFocusChanged { if (it.hasFocus) onCategoryFocus() }
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
            fontSize = fontSize,
            lineHeight = 15.sp,
            fontWeight = fontWeight
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
private fun PreviewSideBarFocused() {
    TripleRockTVTheme {
        SideBar(
            focusedCategory = 56,
            focus = FocusItem.SideBar
        )
    }
}

@TvPreview
@Composable
private fun PreviewSideBar() {
    TripleRockTVTheme {
        SideBar(
            focusedCategory = 56,
            focus = FocusItem.Content
        )
    }
}