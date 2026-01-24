package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

const val cardContainerHeight = 185
const val cardContainerWidth = 145
const val cardHeight = cardContainerHeight - 15
const val cardWidth = cardContainerWidth - 15
const val focusChange = 5


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ThumbnailCard(
    streamEntity: StreamEntity,
    onClick: () -> Unit = {}
) {
    var focused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .width(cardContainerWidth.dp)
            .height(cardContainerHeight.dp)
            .clickable(
                indication = null,
                interactionSource = null
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(
            onClick = { onClick() },
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = if (focused) 6.dp else 2.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface,
            )
        ) {
            Column(
                modifier = Modifier
                    .width(if (focused) (cardWidth + focusChange).dp else cardWidth.dp)
                    .height(if (focused) (cardHeight + focusChange).dp else cardHeight.dp)
                    .clip(RoundedCornerShape(5.dp)),
            ) {
                GlideImage(
                    model = streamEntity.streamIcon,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.weight(1f)
                )
                Footer(streamEntity)
            }
        }
    }
}

@Composable
fun Footer(streamEntity: StreamEntity) {
    Column(
        modifier = Modifier
            .height(43.dp)
            .padding(5.dp)
    ) {
        Text(
            streamEntity.name,
            color = colorScheme.onBackground,
            fontSize = 12.sp,
            lineHeight = 15.sp,
        )
    }
}

@TvPreview
@Composable
private fun PreviewThumbnailCard() {
    TripleRockTVTheme {
        ThumbnailCard(StreamEntity.sample())
    }
}