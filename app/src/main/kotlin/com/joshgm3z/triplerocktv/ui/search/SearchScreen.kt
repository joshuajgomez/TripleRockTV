package com.joshgm3z.triplerocktv.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.BackButton
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.home.Content
import com.joshgm3z.triplerocktv.ui.home.FocusItem
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun SearchScreen(
    openMediaInfoScreen: (StreamEntity) -> Unit = {},
    goBack: () -> Unit = {},
) {
    Row(modifier = Modifier.fillMaxSize()) {
        BackButton { goBack() }
        Column {
            OutlinedTextField(
                "",
                placeholder = {
                    Text("Search movies, series or live tv")
                },
                onValueChange = {},
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.size(10.dp))
            Content(
                setFocus = {},
                focus = FocusItem.Content,
                onContentClick = { openMediaInfoScreen(it) }
            )
        }
    }
}

@Composable
@TvPreview
fun PreviewSearchScreen() {
    TripleRockTVTheme {
        SearchScreen()
    }
}