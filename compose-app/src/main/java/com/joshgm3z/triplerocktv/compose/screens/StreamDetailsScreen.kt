package com.joshgm3z.triplerocktv.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.joshgm3z.triplerocktv.compose.NavMainDestination
import com.joshgm3z.triplerocktv.compose.R
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.MetadataBar
import com.joshgm3z.triplerocktv.compose.screens.common.PrimaryButton
import com.joshgm3z.triplerocktv.compose.screens.common.SecondaryButton
import com.joshgm3z.triplerocktv.compose.screens.settings.appBottomPadding
import com.joshgm3z.triplerocktv.compose.screens.settings.appHorizontalPadding
import com.joshgm3z.triplerocktv.compose.screens.settings.appTopPadding
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsUiState
import com.joshgm3z.triplerocktv.core.viewmodel.DetailsViewModel
import kotlin.contracts.ExperimentalContracts

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

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalContracts::class)
@Composable
private fun StreamDetailsScreenContent(
    uiState: DetailsUiState,
    onPlayClicked: (resume: Boolean) -> Unit = {},
    setFavorite: (add: Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    onMoreEpisodesClick: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GlideImage(
            model = uiState.coverImage.orPreview(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(
                    horizontal = appHorizontalPadding,
                    vertical = appTopPadding
                )
                .background(
                    color = colorScheme.onBackground.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = colorScheme.background
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(brush = darkOverlayBrush())
                .fillMaxSize()
                .padding(
                    start = appHorizontalPadding,
                    end = appHorizontalPadding,
                    top = 250.dp,
                    bottom = appBottomPadding
                )
        ) {
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
                })
            if (uiState.streamType == StreamType.Series && !uiState.subtitle.isNullOrEmpty()) Text(
                text = uiState.subtitle!!,
                color = colorScheme.primary,
                style = typography.labelMedium
            )
            uiState.description?.let {
                Text(
                    text = it,
                    color = colorScheme.onBackground.copy(alpha = 0.8f),
                    style = typography.bodyMedium
                )
            }
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
            Spacer(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
            if (uiState.showButtons) ButtonContainer(
                uiState,
                onPlayClicked = onPlayClicked,
                setFavorite = setFavorite,
                moreEpisodes = onMoreEpisodesClick
            )
        }
    }
}

fun String?.ifNotNullOrEmpty(block: (String) -> Unit) {
    if (!isNullOrEmpty()) block(this)
}

@Composable
private fun ButtonContainer(
    uiState: DetailsUiState,
    onPlayClicked: (resume: Boolean) -> Unit = {},
    setFavorite: (add: Boolean) -> Unit = {},
    moreEpisodes: () -> Unit = {},
) {
    val textAlign = TextAlign.Start
    Column {
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

@Composable
fun darkOverlayBrush() = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        colorScheme.background,
        colorScheme.background,
        colorScheme.background,
    ),
    // Start the gradient at 40% of the container height
    startY = 0.1f
)

@Composable
private fun String?.orPreview(): Any? = when {
    LocalInspectionMode.current -> R.drawable.backdrop_office
    else -> this
}

@DarkPreview
@Composable
private fun PreviewStreamDetailsScreen() {
    DarkSurface {
        StreamDetailsScreenContent(
            DetailsUiState(
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
        )
    }
}

@DarkPreview
@Composable
private fun PreviewButtonContainer_Vod() {
    DarkSurface {
        ButtonContainer(
            DetailsUiState(
                streamType = StreamType.VideoOnDemand,
                title = "Inception (2010)",
                showButtons = true,
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewButtonContainer_Series() {
    DarkSurface {
        ButtonContainer(
            DetailsUiState(
                streamType = StreamType.Series,
                title = "Inception (2010)",
                showButtons = true,
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewButtonContainer_resume_remove() {
    DarkSurface {
        ButtonContainer(
            DetailsUiState(
                streamType = StreamType.VideoOnDemand,
                title = "Inception (2010)",
                showButtons = true,
                progressPercent = 50,
                favorite = true
            )
        )
    }
}