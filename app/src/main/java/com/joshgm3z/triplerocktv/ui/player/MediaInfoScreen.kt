package com.joshgm3z.triplerocktv.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Gray10
import com.joshgm3z.triplerocktv.ui.theme.Green10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun MediaInfoScreen() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (thumbNail, title, year, resume, startOver, description, back, subtitle) = createRefs()

        BackButton(modifier = Modifier.constrainAs(back) {
            start.linkTo(parent.start, margin = 100.dp)
            top.linkTo(parent.top, margin = 100.dp)
        }) {}

        ContentTitle(modifier = Modifier.constrainAs(title) {
            start.linkTo(back.start)
            top.linkTo(back.bottom, 80.dp)
        })

        ContentYear(modifier = Modifier.constrainAs(year) {
            start.linkTo(title.start)
            top.linkTo(title.bottom, 10.dp)
        })

        MovieDescription(modifier = Modifier.constrainAs(description) {
            start.linkTo(title.start)
            top.linkTo(year.bottom, 50.dp)
        })

        ThumbnailEnlarged(modifier = Modifier.constrainAs(thumbNail) {
            end.linkTo(parent.end, 100.dp)
            top.linkTo(back.top)
        })

        SubtitleInfo(modifier = Modifier.constrainAs(subtitle) {
            start.linkTo(back.start)
            bottom.linkTo(startOver.top, 20.dp)
        })

        ResumeButton(modifier = Modifier.constrainAs(resume) {
            start.linkTo(back.start)
            bottom.linkTo(thumbNail.bottom)
        }) {}

        StartOverButton(modifier = Modifier.constrainAs(startOver) {
            start.linkTo(back.start)
            bottom.linkTo(resume.top, 20.dp)
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
            .width(400.dp)
            .height(80.dp)
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
            .width(400.dp)
            .height(80.dp),
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
fun MovieDescription(modifier: Modifier) {
    Text(
        text = "Second installment to Avatar, this is a blockbuster from James Cameron. Second installment to Avatar, this is a blockbuster from James Cameron. Second installment to Avatar, this is a blockbuster from James Cameron.",
        modifier = modifier.width(700.dp),
        fontSize = 25.sp,
        color = colorScheme.onBackground.copy(alpha = 0.8f),
        lineHeight = 35.sp
    )
}

@Composable
fun ContentYear(modifier: Modifier) {
    Text(
        text = "2007",
        modifier = modifier,
        fontSize = 25.sp,
        color = colorScheme.onBackground.copy(alpha = 0.5f)
    )
}

@Composable
fun ContentTitle(modifier: Modifier) {
    Text(
        text = "Avatar: The Way of Water",
        modifier = modifier,
        fontSize = 50.sp,
        color = colorScheme.onBackground
    )
}

@Composable
fun BackButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = { onClick() },
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = Gray10)
            .padding(horizontal = 20.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null
        )
        Spacer(Modifier.size(10.dp))
        Text(
            "Go back",
            color = colorScheme.onBackground
        )
    }
}

@Composable
fun ThumbnailEnlarged(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.avatar_movie),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .width(600.dp)
            .height(800.dp)
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