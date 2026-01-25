package com.joshgm3z.triplerocktv.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Gray20
import com.joshgm3z.triplerocktv.ui.theme.Gray800
import com.joshgm3z.triplerocktv.ui.theme.Gray900
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.HomeViewModel
import com.joshgm3z.triplerocktv.viewmodel.IHomeViewModel

@Composable
fun Content(
    modifier: Modifier = Modifier,
    categoryId: Int = 1,
    focus: FocusItem,
    viewModel: IHomeViewModel = getHomeViewModel(),
    onContentClick: (StreamEntity) -> Unit = {},
    setFocus: (FocusItem) -> Unit = {},
) {
    val contentFocusRequester = remember { FocusRequester() }
    LaunchedEffect(focus) {
        if (focus == FocusItem.Content) contentFocusRequester.requestFocus()
    }

    viewModel.fetchContent(categoryId)
    val uiState = viewModel.uiState.collectAsState().value
    Log.i("Content", "Content: ${uiState.mediaList.size}")
    FlowRow(
        modifier = modifier
            .focusRequester(contentFocusRequester)
            .onFocusChanged {
                if (it.hasFocus) setFocus(FocusItem.Content)
            }
            .layoutId(HomeScreenLayoutId.Content)
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp),
        maxItemsInEachRow = 5,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        uiState.mediaList.forEach {
            ThumbnailCard(it) { onContentClick(it) }
        }
    }
}

@TvPreview
@Composable
fun ContentPlaceholder(text: String = "Fetching movies") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(700.dp)
            .height(400.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.movie),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.size(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(10.dp))
            Text(text)
        }
    }
}

@TvPreview
@Composable
private fun PreviewContent() {
    TripleRockTVTheme {
//        Content()
    }
}