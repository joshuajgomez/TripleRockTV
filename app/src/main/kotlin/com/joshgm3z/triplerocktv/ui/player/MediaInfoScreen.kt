package com.joshgm3z.triplerocktv.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.common.BackButton
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.Green10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import com.joshgm3z.triplerocktv.viewmodel.FakeMediaInfoViewModel
import com.joshgm3z.triplerocktv.viewmodel.IMediaInfoViewModel
import com.joshgm3z.triplerocktv.viewmodel.MediaInfoViewModel

@Composable
fun getMediaInfoViewModel() = when {
    LocalInspectionMode.current -> FakeMediaInfoViewModel()
    else -> hiltViewModel<MediaInfoViewModel>()
}

@Composable
fun MediaInfoScreen(
    viewModel: IMediaInfoViewModel = getMediaInfoViewModel(),
    goBack: () -> Unit = {},
) {
    val resumeFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { resumeFocusRequester.requestFocus() }
    val streamEntity = viewModel.uiState.collectAsState().value.streamEntity ?: return
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (info, back, wallpaper) = createRefs()

        Wallpaper(modifier = Modifier.constrainAs(wallpaper) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
        }, streamEntity.streamIcon)

        InfoContainer(
            modifier = Modifier.constrainAs(info) {
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            },
            streamEntity = streamEntity,
        )

        BackButton(modifier = Modifier.constrainAs(back) {
            start.linkTo(parent.start, 10.dp)
            top.linkTo(parent.top, 10.dp)
        }) { goBack() }
    }
}

val gradientBrush = Brush.radialGradient(
    colors = listOf(
        Color.Black,
        Color.Black,
        Color.Transparent,
    ),
    center = Offset(100f, Float.POSITIVE_INFINITY),
    radius = 1500f
)

@Composable
fun InfoContainer(
    modifier: Modifier = Modifier,
    streamEntity: StreamEntity,
) {
    Column(
        modifier = modifier
            .size(800.dp)
            .background(brush = gradientBrush)
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(Modifier.padding(start = 10.dp)) {
            Row {
                Thumbnail(streamEntity.streamIcon ?: "")
                Spacer(Modifier.size(10.dp))
                Column() {
                    ContentTitle(streamEntity.name)
                    Spacer(Modifier.size(5.dp))
                    Text(
                        "120 min",
                        color = colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 15.sp,
                    )
                    Spacer(Modifier.size(10.dp))
                    SubtitleInfo()
                }
            }
            Spacer(Modifier.size(20.dp))
            ResumeButton(onClick = {})
            Spacer(Modifier.size(10.dp))
            StartOverButton() {}
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Wallpaper(
    modifier: Modifier = Modifier,
    icon: String?
) {
    icon?.let {
        GlideImage(
            model = it,
            loading = placeholder {
                /*Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                ) {}*/
                Image(
                    painter = painterResource(R.drawable.avatar_movie),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            },
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
        )
    }
}

@Composable
fun SubtitleInfo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
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
fun Modifier.buttonModifier(focus: Boolean) =
    clip(RoundedCornerShape(5.dp))
        .width(200.dp)
        .height(40.dp)
        .background(
            color = if (focus) colorScheme.onBackground
            else colorScheme.background
        )

@Composable
fun colorFg(focus: Boolean) = when {
    focus -> colorScheme.background
    else -> colorScheme.onBackground
}

@Composable
fun ResumeButton(
    text: String = "Resume",
    onClick: () -> Unit,
) {
    var focus by remember { mutableStateOf(false) }
    TextButton(
        { onClick() },
        modifier = Modifier
            .buttonModifier(focus)
            .onFocusChanged { focus = it.isFocused },
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            tint = colorFg(focus)
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = text,
            color = colorFg(focus)
        )
    }
}

@Composable
fun StartOverButton(
    text: String = "Start over",
    onClick: () -> Unit,
) {
    var focus by remember { mutableStateOf(false) }
    TextButton(
        { onClick() },
        modifier = Modifier
            .buttonModifier(focus)
            .onFocusChanged { focus = it.isFocused },
        elevation = ButtonDefaults.buttonElevation()
    ) {
        Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            tint = colorFg(focus)
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = text,
            color = colorFg(focus)
        )
    }
}

@Composable
fun ContentTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = colorScheme.onBackground
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Thumbnail(iconUrl: String) {
    GlideImage(
        model = iconUrl,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        loading = placeholder {
            /*Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                ) {}*/
            Image(
                painter = painterResource(R.drawable.avatar_movie),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        },
        modifier = Modifier
            .width(80.dp)
            .height(115.dp)
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