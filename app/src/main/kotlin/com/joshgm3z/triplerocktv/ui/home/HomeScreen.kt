package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun HomeScreen(
    openMediaInfoScreen: (StreamEntity) -> Unit = {},
    openSearchScreen: () -> Unit = {},
) {
    var selectedTopbarItem: TopbarItem by remember { mutableStateOf(TopbarItem.Movies) }
    var selectedCategory: Int by remember { mutableIntStateOf(0) }

    ConstraintLayout(
        constraintSet = getHomeScreenConstraints(),
    ) {
        TopBar {
            if (it == TopbarItem.Search) openSearchScreen()
            else selectedTopbarItem = it
        }
        SideBar(selected = selectedCategory) {
            selectedCategory = it
        }
        Content(categoryId = selectedCategory) {
            openMediaInfoScreen(it)
        }
        MenuIcon()
    }
}

@Composable
fun MenuIcon(onMenuClick: () -> Unit = {}) {
    IconButton(
        onClick = { onMenuClick() },
        modifier = Modifier.layoutId(HomeScreenLayoutId.Menu)
    ) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = null
        )
    }
}

@Composable
@TvPreview
private fun PreviewHomeScreen() {
    TripleRockTVTheme {
        HomeScreen()
    }
}