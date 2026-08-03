package com.joshgm3z.triplerocktv.compose.screens

import android.R.attr.top
import android.R.id.closeButton
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.screens.common.CloseButton
import com.joshgm3z.triplerocktv.compose.screens.common.DarkLandscapePreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.GlidePic
import com.joshgm3z.triplerocktv.compose.screens.common.MetadataBar
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.screens.common.SecondaryButton
import com.joshgm3z.triplerocktv.compose.screens.common.appBottomPadding
import com.joshgm3z.triplerocktv.compose.screens.common.appTopPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.util.ifNotNullOrEmpty
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsUiState
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsViewModel

@Composable
fun StreamDetailsScreen(
    viewModel: DetailsViewModel = hiltViewModel(),
    navigateMain: (NavMainDestination) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    viewModel.uiState.collectAsState().value?.let { uiState ->
        StreamDetailsScreenContent(
            uiState = uiState,
            onPlayClicked = { resume ->
                val navDirection = when (uiState.streamType) {
                    StreamType.Series -> NavMainDestination.Playback(
                        streamId = uiState.episodeId!!,
                        streamType = uiState.streamType,
                        seriesId = viewModel.streamId,
                        resume = resume
                    )

                    else -> NavMainDestination.Playback(
                        streamId = viewModel.streamId,
                        streamType = uiState.streamType,
                        resume = resume
                    )
                }
                navigateMain(navDirection)
            },
            setFavorite = {
                viewModel.updateMyList(it)
            },
            onBackClick = onBackClick,
            onMoreEpisodesClick = {
                navigateMain(
                    NavMainDestination.EpisodeSelector(
                        seriesId = viewModel.streamId,
                        initialSelectedEpisodeId = uiState.episodeId
                    )
                )
            }
        )
    }
}

private enum class LayoutId {
    Poster,
    CloseButton,
    TextColumn,
    Buttons
}

private fun getConstraints(isLandscape: Boolean = false) = ConstraintSet {
    val poster = createRefFor(LayoutId.Poster)
    val closeButton = createRefFor(LayoutId.CloseButton)
    val textColumn = createRefFor(LayoutId.TextColumn)
    val buttons = createRefFor(LayoutId.Buttons)

    if (isLandscape) {
        constrain(poster) {
            top.linkTo(parent.top, margin = 50.dp)
            start.linkTo(parent.start, margin = 15.dp)
            width = Dimension.value(450.dp)
            height = Dimension.value(250.dp)
        }
        constrain(closeButton) {
            top.linkTo(poster.top, margin = 15.dp)
            start.linkTo(poster.start, margin = 15.dp)
        }
        constrain(textColumn) {
            top.linkTo(poster.top)
            start.linkTo(poster.end, margin = 15.dp)
            end.linkTo(parent.end, margin = 15.dp)
            width = Dimension.fillToConstraints
        }
        constrain(buttons) {
            bottom.linkTo(parent.bottom, margin = appBottomPadding)
            start.linkTo(textColumn.start)
            end.linkTo(textColumn.end)
            width = Dimension.fillToConstraints
        }
    } else {
        constrain(poster) {
            top.linkTo(parent.top, margin = appTopPadding)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.value(220.dp)
        }
        constrain(closeButton) {
            top.linkTo(poster.top, margin = 15.dp)
            start.linkTo(textColumn.start)
        }
        constrain(textColumn) {
            top.linkTo(poster.bottom, margin = 15.dp)
            start.linkTo(parent.start, margin = 15.dp)
            end.linkTo(parent.end, margin = 15.dp)
            width = Dimension.fillToConstraints
        }
        constrain(buttons) {
            bottom.linkTo(parent.bottom, margin = appBottomPadding)
            start.linkTo(textColumn.start)
            end.linkTo(textColumn.end)
            width = Dimension.fillToConstraints
        }
    }
}

@Composable
private fun StreamDetailsScreenContent(
    uiState: DetailsUiState,
    onPlayClicked: (resume: Boolean) -> Unit = {},
    setFavorite: (add: Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    onMoreEpisodesClick: () -> Unit = {},
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    ConstraintLayout(
        constraintSet = getConstraints(isLandscape),
        modifier = Modifier.fillMaxSize()
    ) {
        val posterModifier = if (isLandscape) Modifier.clip(RoundedCornerShape(15.dp))
        else Modifier
        GlidePic(
            model = uiState.coverImage,
            modifier = posterModifier.layoutId(LayoutId.Poster)
        )
        CloseButton(
            onClick = onBackClick,
            showBackground = true,
            modifier = Modifier.layoutId(LayoutId.CloseButton)
        )
        Column(modifier = Modifier.layoutId(LayoutId.TextColumn)) {
            Text(
                text = uiState.title,
                color = colorScheme.onBackground,
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            MetadataBar(
                rating = uiState.rating,
                favorite = uiState.favorite,
                list = mutableListOf<String>().apply {
                    if (uiState.streamType == StreamType.VideoOnDemand)
                        uiState.subtitle.ifNotNullOrEmpty { add(it) }
                    uiState.duration.ifNotNullOrEmpty { add(it) }
                }
            )
            if (uiState.streamType == StreamType.Series && !uiState.subtitle.isNullOrEmpty()) Text(
                text = uiState.subtitle!!,
                color = colorScheme.primary,
                style = typography.labelMedium
            )
            Spacer(Modifier.size(10.dp))
            uiState.description?.let {
                Text(
                    text = it,
                    color = colorScheme.onBackground.copy(alpha = 0.8f),
                    style = typography.bodyMedium
                )
            }
            Spacer(Modifier.size(10.dp))
            uiState.cast?.let {
                Text(
                    text = it,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                    style = typography.bodyMedium
                )
            }
            uiState.director?.let {
                Text(
                    text = it,
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                    style = typography.bodyMedium
                )
            }
        }

        if (uiState.showButtons) ButtonContainer(
            uiState = uiState,
            onPlayClicked = onPlayClicked,
            setFavorite = setFavorite,
            moreEpisodes = onMoreEpisodesClick,
            modifier = Modifier.layoutId(LayoutId.Buttons)
        )
    }
}

@Composable
private fun ButtonContainer(
    modifier: Modifier = Modifier,
    uiState: DetailsUiState,
    onPlayClicked: (resume: Boolean) -> Unit = {},
    setFavorite: (add: Boolean) -> Unit = {},
    moreEpisodes: () -> Unit = {},
) {
    val textAlign = TextAlign.Start
    Column(modifier = modifier.background(color = colorScheme.background)) {
        PrimaryButton(
            visible = uiState.progressPercent == null,
            text = "Play",
            imageVector = Icons.Default.PlayArrow,
            onClick = { onPlayClicked(false) },
            textAlign = textAlign,
        )
        if (uiState.progressPercent != null) Box(
            contentAlignment = Alignment.CenterEnd
        ) {
            PrimaryButton(
                text = "Resume",
                imageVector = Icons.Default.PlayArrow,
                onClick = { onPlayClicked(true) },
                textAlign = textAlign,
            )
            LinearProgressIndicator(
                progress = { uiState.progressPercent!! / 100f },
                modifier = Modifier
                    .height(4.dp)
                    .width(150.dp)
                    .padding(end = 30.dp),
                drawStopIndicator = {},
                color = colorScheme.onPrimary,
                trackColor = colorScheme.onPrimary.copy(alpha = 0.3f),
            )
        }
        SecondaryButton(
            visible = uiState.progressPercent != null,
            text = "Start over",
            imageVector = Icons.Default.RestartAlt,
            onClick = { onPlayClicked(false) },
            textAlign = textAlign,
        )
        if (uiState.streamType == StreamType.Series) SecondaryButton(
            text = "More episodes",
            onClick = moreEpisodes,
            imageVector = Icons.Default.VideoLibrary,
            textAlign = textAlign,
        )
        SecondaryButton(
            visible = !uiState.favorite,
            text = "Add to favorites",
            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
            onClick = { setFavorite(true) },
            textAlign = textAlign,
        )
        SecondaryButton(
            visible = uiState.favorite,
            text = "Remove from favorites",
            imageVector = Icons.Default.PlaylistRemove,
            onClick = { setFavorite(false) },
            textAlign = textAlign,
        )
    }
}

private val sampleUiState = DetailsUiState(
    streamType = StreamType.VideoOnDemand,
    coverImage = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
    title = "Inception (2010)",
    description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
    cast = "Cast: Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page",
    director = "Director: Christopher Nolan",
    rating = 8.3f,
    showButtons = true,
    subtitle = "Drama, Thriller",
    favorite = true,
    duration = "1h 2m"
)

@DarkPreview
@Composable
private fun PreviewStreamDetailsScreen() {
    DarkSurface {
        StreamDetailsScreenContent(sampleUiState)
    }
}

@DarkLandscapePreview
@Composable
private fun PreviewStreamDetailsScreen_Landscape() {
    DarkSurface {
        StreamDetailsScreenContent(sampleUiState)
    }
}

//@DarkPreview
@Composable
private fun PreviewButtonContainer_Vod() {
    DarkSurface {
        ButtonContainer(
            uiState = DetailsUiState(
                streamType = StreamType.VideoOnDemand,
                title = "Inception (2010)",
                showButtons = true,
            )
        )
    }
}

//@DarkPreview
@Composable
private fun PreviewButtonContainer_Series() {
    DarkSurface {
        ButtonContainer(
            uiState = DetailsUiState(
                streamType = StreamType.Series,
                title = "Inception (2010)",
                showButtons = true,
            )
        )
    }
}

//@DarkPreview
@Composable
private fun PreviewButtonContainer_resume_remove() {
    DarkSurface {
        ButtonContainer(
            uiState = DetailsUiState(
                streamType = StreamType.VideoOnDemand,
                title = "Inception (2010)",
                showButtons = true,
                progressPercent = 50,
                favorite = true
            )
        )
    }
}