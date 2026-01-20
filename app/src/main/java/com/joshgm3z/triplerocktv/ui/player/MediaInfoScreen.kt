package com.joshgm3z.triplerocktv.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.common.BackButton
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.home.MediaItem
import com.joshgm3z.triplerocktv.ui.theme.Green10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun MediaInfoScreen(
    mediaItem: MediaItem = MediaItem.sample(),
    goBack: () -> Unit = {},
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (thumbNail, title, year, resume, startOver, description, back, subtitle) = createRefs()

        BackButton(modifier = Modifier.constrainAs(back) {
            start.linkTo(parent.start, margin = 20.dp)
            top.linkTo(parent.top, margin = 20.dp)
        }) { goBack() }

        ContentTitle(modifier = Modifier.constrainAs(title) {
            start.linkTo(back.start)
            top.linkTo(back.bottom, 30.dp)
        }, mediaItem.title)

        ContentYear(modifier = Modifier.constrainAs(year) {
            start.linkTo(title.start)
            top.linkTo(title.bottom, 10.dp)
        }, mediaItem.year)

        MovieDescription(modifier = Modifier.constrainAs(description) {
            start.linkTo(title.start)
            top.linkTo(year.bottom, 20.dp)
        }, mediaItem.description)

        ThumbnailEnlarged(modifier = Modifier.constrainAs(thumbNail) {
            end.linkTo(parent.end, 100.dp)
            top.linkTo(back.top)
        }, mediaItem.thumbNail)

        SubtitleInfo(modifier = Modifier.constrainAs(subtitle) {
            start.linkTo(back.start)
            bottom.linkTo(startOver.top, 10.dp)
        })

        ResumeButton(modifier = Modifier.constrainAs(resume) {
            start.linkTo(back.start)
            bottom.linkTo(thumbNail.bottom)
        }) {}

        StartOverButton(modifier = Modifier.constrainAs(startOver) {
            start.linkTo(back.start)
            bottom.linkTo(resume.top, 10.dp)
        }) {}

    }
}

@Composable
fun SubtitleInfo(modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Green10
        )
        Spacer(Modifier.size(10.dp))
        Text(
            "5 subtitles found",
            color = colorScheme.onBackground
        )
    }
}

@Composable
fun ResumeButton(
    modifier: Modifier,
    text: String = "Resume",
    onClick: () -> Unit,
) {
    TextButton(
        { onClick() },
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = colorScheme.primaryContainer)
            .width(300.dp)
            .height(50.dp)
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            tint = colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            color = colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun StartOverButton(
    modifier: Modifier,
    text: String = "Start over",
    onClick: () -> Unit,
) {
    TextButton(
        { onClick() },
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = colorScheme.onSecondary)
            .width(300.dp)
            .height(50.dp),
    ) {
        Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            tint = colorScheme.secondary
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            color = colorScheme.secondary
        )
    }
}

@Composable
fun MovieDescription(
    modifier: Modifier,
    description: String,
) {
    Text(
        text = description,
        modifier = modifier.width(300.dp),
        fontSize = 10.sp,
        color = colorScheme.onBackground.copy(alpha = 0.8f),
        lineHeight = 25.sp
    )
}

@Composable
fun ContentYear(
    modifier: Modifier,
    year: String,
) {
    Text(
        text = year,
        modifier = modifier,
        fontSize = 20.sp,
        color = colorScheme.onBackground.copy(alpha = 0.5f)
    )
}

@Composable
fun ContentTitle(
    modifier: Modifier,
    title: String,
) {
    Text(
        text = title,
        modifier = modifier,
        fontSize = 20.sp,
        color = colorScheme.onBackground
    )
}

@Composable
fun ThumbnailEnlarged(
    modifier: Modifier,
    resId: Int,
) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .width(300.dp)
            .height(450.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorScheme.error)
    )
}

@TvPreview
@Composable
private fun PreviewMediaInfoScreen() {
    TripleRockTVTheme {
        MediaInfoScreen()
    }
}