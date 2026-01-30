package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerScope
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
    onSelectedCategoryUpdate: (CategoryEntity) -> Unit,
    closeDrawer: () -> Unit,
    focusRestorer: FocusRequester,
): @Composable NavigationDrawerScope.(DrawerValue) -> Unit = {
    Column(
        modifier = Modifier.padding(top = 20.dp)
    ) {
        TopbarUser(
            hasFocus = hasFocus,
        )
        TopbarHome(
            hasFocus = hasFocus,
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
                        leadingContent = {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null
                            )
                        },
                    )
                }
            }
    }
}

val topIconModifier = Modifier
    .height(50.dp)
    .padding(
        bottom = 15.dp,
        start = 12.dp
    )

@Composable
fun TopbarUser(
    hasFocus: Boolean,
) {
    Row(
        modifier = topIconModifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.avatar_movie),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        if (hasFocus) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Someone")
        }
    }
}

@Composable
fun TopbarHome(
    hasFocus: Boolean,
) {
    Row(
        modifier = topIconModifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape),
        )
        if (hasFocus) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Home")
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