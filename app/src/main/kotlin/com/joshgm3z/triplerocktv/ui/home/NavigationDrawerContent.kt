package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity

@Composable
fun NavigationDrawerContent(
    uiState: HomeUiState,
    onTopbarItemUpdate: (TopbarItem) -> Unit,
    onSelectedCategoryUpdate: (CategoryEntity) -> Unit,
    closeDrawer: () -> Unit,
    focusRestorer: FocusRequester,
): @Composable NavigationDrawerScope.(DrawerValue) -> Unit = {
    Column(
        modifier = Modifier.padding(
            start = 10.dp,
            top = 20.dp
        )
    ) {
        TopbarUser(
            hasFocus = hasFocus,
        )
        TopbarMenu(
            hasFocus = hasFocus,
            selectedTopbarItem = uiState.selectedTopbarItem,
            onTopbarItemUpdate = onTopbarItemUpdate
        )
        if (!uiState.categoryEntities.isEmpty())
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .focusGroup()
                    .focusRestorer(focusRestorer)
            ) {
                items(uiState.categoryEntities) { categoryEntity ->
                    NavigationDrawerItem(
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) onSelectedCategoryUpdate(categoryEntity)
                        },
                        selected = uiState.selectedCategoryEntity == categoryEntity,
                        onClick = { closeDrawer() },
                        content = { Text(text = categoryEntity.categoryName) },
                        leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                    )
                }
            }
    }
}

val topIconModifier = Modifier
    .defaultMinSize(minWidth = 56.dp)
    .height(50.dp)
    .padding(bottom = 15.dp)

@Composable
fun TopbarMenu(
    hasFocus: Boolean,
    selectedTopbarItem: TopbarItem?,
    onTopbarItemUpdate: (TopbarItem) -> Unit
) {
    Row(
        modifier = topIconModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(hasFocus) {
            if (it) {
                TopMenuDropDown(selectedTopbarItem) { item ->
                    onTopbarItemUpdate(item)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun TopbarUser(
    hasFocus: Boolean,
) {
    Row(
        modifier = topIconModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(hasFocus) {
            if (it) {
                UserDropDown(TopbarItem.Search) {}
            } else {
                Image(
                    painter = painterResource(R.drawable.avatar_movie),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun TopMenuDropDown(
    selectedTopbarItem: TopbarItem?,
    onSelectionChange: (TopbarItem) -> Unit
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.width(150.dp)
            ) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(selectedTopbarItem?.name ?: "Select")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TopbarItem.entries.forEach { item ->
                    DropdownMenuItem(
                        text = { androidx.compose.material3.Text(item.name) },
                        onClick = {
                            onSelectionChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserDropDown(
    selectedTopbarItem: TopbarItem?,
    onSelectionChange: (TopbarItem) -> Unit
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.width(150.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar_movie),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("someone")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TopbarItem.entries.forEach { item ->
                    DropdownMenuItem(
                        text = { androidx.compose.material3.Text(item.name) },
                        onClick = {
                            onSelectionChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}