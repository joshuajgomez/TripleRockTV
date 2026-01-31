package com.joshgm3z.triplerocktv.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.CompactCard
import androidx.tv.material3.Text
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

const val cardContainerHeight = 150
const val cardContainerWidth = 105


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ThumbnailCard(
    streamEntity: StreamEntity,
    onClick: () -> Unit = {}
) {
    CompactCard(
        modifier = Modifier
            .width(cardContainerWidth.dp)
            .height(cardContainerHeight.dp),
        onClick = { onClick() },
        image = {
            GlideImage(
                model = streamEntity.streamIcon,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier,
                /*loading = placeholder {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_movie),
                        contentDescription = null,
                        modifier = Modifier,
                        contentScale = ContentScale.FillWidth
                    )
                }*/
            )
        }, title = {
            Text(
                text = streamEntity.name,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(5.dp),
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    )
}

@TvPreview
@Composable
private fun PreviewThumbnailCard() {
    TripleRockTVTheme {
        ThumbnailCard(StreamEntity.sample())
    }
}