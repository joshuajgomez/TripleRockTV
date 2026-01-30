package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun navigationDrawerContent(
    uiState: HomeUiState,
    onTopbarItemUpdate: (TopbarItem) -> Unit,
    openSettings: () -> Unit = {},
    onSelectedCategoryUpdate: (CategoryEntity) -> Unit,
    closeDrawer: () -> Unit,
    focusRestorer: FocusRequester,
    isOpen: Boolean = false,
): @Composable NavigationDrawerScope.(DrawerValue) -> Unit = {
    Column(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        CustomIconButton(
            expand = hasFocus,
            text = "Someone",
            icon = {
                Image(
                    painter = painterResource(R.drawable.avatar_movie),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        )
        CustomIconButton(
            expand = hasFocus,
            text = "Home",
            icon = { fgColor ->
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    tint = fgColor
                )
            }
        )

        if (!uiState.categoryEntities.isEmpty())
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .focusGroup()
                    .weight(1f)
                    .focusRestorer(focusRestorer)
            ) {
                items(uiState.categoryEntities) { categoryEntity ->
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) onSelectedCategoryUpdate(categoryEntity)
                        },
                        selected = uiState.selectedCategoryEntity == categoryEntity,
                        onClick = { closeDrawer() },
                        content = { Text(text = categoryEntity.categoryName) },
                        leadingContent = {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null
                            )
                        },
                    )
                }
            }

        CustomIconButton(
            expand = hasFocus,
            text = "Settings",
            icon = { fgColor ->
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = fgColor
                )
            },
            onClick = { openSettings() }
        )
    }
}

@Composable
fun CustomIconButton(
    expand: Boolean,
    text: String,
    onClick: () -> Unit = {},
    icon: @Composable (color: Color) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val fgColor = when {
        focused -> colorScheme.background
        else -> colorScheme.onSurface
    }
    val bgColor = when {
        focused -> colorScheme.onBackground
        expand -> colorScheme.background
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .height(50.dp)
            .padding(bottom = 15.dp, start = 5.dp)
            .onFocusChanged { focused = it.isFocused }
            .clip(RoundedCornerShape(10.dp))
            .clickable(true) { onClick() }
            .background(color = bgColor)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon(fgColor)
        if (expand) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text, color = fgColor)
        }
    }
}

@Composable
@TvPreview
private fun PreviewNavigationDrawerContent() {
    TripleRockTVTheme {
        val drawerValue = rememberDrawerState(DrawerValue.Open)
        NavigationDrawer(
            drawerState = drawerValue,
            drawerContent = navigationDrawerContent(
                uiState = HomeUiState(categoryEntities = CategoryEntity.samples()),
                onTopbarItemUpdate = {},
                onSelectedCategoryUpdate = {},
                closeDrawer = {},
                focusRestorer = remember { FocusRequester() },
            )
        ) { }
    }
}

@Composable
@TvPreview
private fun PreviewNavigationDrawerContent_Closed() {
    TripleRockTVTheme {
        NavigationDrawer(
            drawerContent = navigationDrawerContent(
                uiState = HomeUiState(categoryEntities = CategoryEntity.samples()),
                onTopbarItemUpdate = {},
                onSelectedCategoryUpdate = {},
                closeDrawer = {},
                focusRestorer = remember { FocusRequester() },
            )
        ) { }
    }
}