package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun SideBar(
    modifier: Modifier = Modifier,
    focusedCategory: Int = 0,
    uiState: HomeUiState.Ready,
    onCategoryFocus: (Int) -> Unit = {},
    onClick: () -> Unit = {},
    focus: FocusItem = FocusItem.SideBar,
    setFocus: (FocusItem) -> Unit = {},
) {
    val sidebarFocusRequester = remember { FocusRequester() }

    LaunchedEffect(focus) {
        if (focus == FocusItem.SideBar) sidebarFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .focusRequester(sidebarFocusRequester)
            .onFocusChanged {
                if (it.hasFocus) setFocus(FocusItem.SideBar)
            }
            .layoutId(HomeScreenLayoutId.SideBar)
    ) {
        if (focus == FocusItem.SideBar) {
            MaximizedSideBar(
                focusedCategory = focusedCategory,
                onCategoryFocus = { onCategoryFocus(it) },
                onClick = { onClick() },
                categories = uiState.categoryEntities
            )
        } else {
            uiState.categoryEntities.firstOrNull { it.categoryId == focusedCategory }?.let {
                MinimizedSideBar(it.categoryName)
            }
        }
    }
}

@Composable
fun MaximizedSideBar(
    focusedCategory: Int,
    onCategoryFocus: (Int) -> Unit = {},
    onClick: () -> Unit = {},
    categories: List<CategoryEntity>
) {
    LazyColumn(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight()
            .background(brush = horizontalGradient)
            .padding(top = 50.dp),
    ) {
        items(categories) {
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
fun MinimizedSideBar(text: String) {
    Box(
        modifier = Modifier
            .layoutId(HomeScreenLayoutId.SideBar)
            .fillMaxHeight()
            .width(300.dp),
    ) {
        Text(
            text = text,
            color = colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier
                .rotateVertically()
                .padding(top = 5.dp, end = 60.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

val horizontalGradient = Brush.horizontalGradient(
    colors = listOf(Color.Black, Color.Black, Color.Transparent)
)

fun Modifier.rotateVertically(clockwise: Boolean = false): Modifier {
    val rotate = rotate(if (clockwise) 90f else -90f)
    val adjustBounds = layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        // Swap the width and height in the layout
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
    return rotate then adjustBounds
}

@Composable
fun SideBarItem(
    category: CategoryEntity,
    focused: Boolean,
    onCategoryFocus: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focused) {
        if (focused) focusRequester.requestFocus()
    }
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
            .fillMaxWidth(0.8f)
            .height(30.dp)
            .focusRequester(focusRequester)
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
            color = colorFg.copy(alpha = 0.7f),
            fontSize = fontSize
        )
    }
}

@TvPreview
@Composable
private fun PreviewSideBarFocused() {
    TripleRockTVTheme {
        SideBar(
            focusedCategory = 56,
            focus = FocusItem.SideBar,
            uiState = HomeUiState.Ready(
                categoryEntities = CategoryEntity.samples(),
                streamEntities = listOf(StreamEntity.sample())
            )
        )
    }
}

@TvPreview
@Composable
private fun PreviewSideBar() {
    TripleRockTVTheme {
        SideBar(
            focusedCategory = 56,
            focus = FocusItem.Content,
            uiState = HomeUiState.Ready(
                categoryEntities = CategoryEntity.samples(),
                streamEntities = listOf(StreamEntity.sample())
            )
        )
    }
}