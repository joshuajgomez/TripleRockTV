package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import kotlinx.coroutines.delay

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StreamStage(
    streams: Collection<List<Any>>,
    onStreamClick: (Int, StreamType) -> Unit = { _, _ -> }
) {
    var stream by remember { mutableStateOf(streams.randomOrNull()?.randomOrNull()) }
    LaunchedEffect(Unit) {
        while (true) {
            stream = streams.randomOrNull()?.randomOrNull()
            delay(8000)
        }
    }
    val poster = when (stream) {
        is StreamData -> (stream as StreamData).streamIcon
        is SeriesStream -> (stream as SeriesStream).coverImageUrl
        else -> return
    }
    val title = when (stream) {
        is StreamData -> (stream as StreamData).name
        is SeriesStream -> (stream as SeriesStream).name
        else -> return
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(650.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlideImage(
            model = poster,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        DarkOverlay()
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = colorScheme.onBackground,
                maxLines = 2,
                textAlign = TextAlign.Center,
                style = typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
            Spacer(Modifier.size(10.dp))
            PrimaryButton(
                onClick = {
                    onStreamClick(
                        (stream as StreamData).streamId,
                        (stream as StreamData).streamType
                    )
                },
                modifier = Modifier.padding(bottom = 20.dp),
                text = "Info",
                imageVector = Icons.Default.Info,
                fillMaxWidth = false
            )
        }
    }
}