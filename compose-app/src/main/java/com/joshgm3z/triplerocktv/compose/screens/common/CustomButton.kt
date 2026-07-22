package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme

private val buttonCornerRadius = 5.dp

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector? = null,
    loading: Boolean = false,
    enabled: Boolean = true,
    visible: Boolean = true,
    fillMaxWidth: Boolean = true,
    textAlign: TextAlign = TextAlign.Center,
    onClick: () -> Unit,
) {
    if (!visible) return
    val modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(buttonCornerRadius),
        onClick = onClick,
        enabled = enabled && !loading
    ) {
        ButtonContent(
            text = text,
            imageVector = imageVector,
            loading = loading,
            textAlign = textAlign,
        )
    }
}

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector? = null,
    loading: Boolean = false,
    enabled: Boolean = true,
    visible: Boolean = true,
    fillMaxWidth: Boolean = true,
    textAlign: TextAlign = TextAlign.Center,
    onClick: () -> Unit
) {
    if (!visible) return
    val modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(buttonCornerRadius),
        onClick = onClick,
        enabled = enabled && !loading
    ) {
        ButtonContent(
            text = text,
            imageVector = imageVector,
            loading = loading,
            textAlign = textAlign
        )
    }
}


@Composable
private fun ButtonContent(
    text: String = "",
    imageVector: ImageVector? = null,
    loading: Boolean = false,
    textAlign: TextAlign
) {
    if (loading) CircularProgressIndicator(
        modifier = Modifier.size(25.dp)
    ) else {
        imageVector?.let {
            Icon(
                imageVector = it, contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(5.dp))
        }
        val modifier = if (textAlign == TextAlign.Center) Modifier
        else Modifier.fillMaxWidth()
        Text(
            modifier = modifier,
            text = text,
            textAlign = textAlign,
            fontWeight = FontWeight.Bold
        )
    }
}

@DarkPreview
@Composable
fun PreviewCustomButton() {
    TripleRockTvTheme {
        Column {
            PrimaryButton(text = "Preview Button") {}
            SecondaryButton(text = "Preview Button") {}
            PrimaryButton(
                text = "Info",
                imageVector = Icons.Default.Info,
                fillMaxWidth = false
            ) {}
            PrimaryButton(
                text = "Resume",
                textAlign = TextAlign.Start,
                imageVector = Icons.Default.PlayArrow
            ) {}
            PrimaryButton(
                text = "Loading",
                loading = true,
            ) {}
        }
    }
}