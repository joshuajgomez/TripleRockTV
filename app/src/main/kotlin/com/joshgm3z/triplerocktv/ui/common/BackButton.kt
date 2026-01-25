package com.joshgm3z.triplerocktv.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.triplerocktv.ui.theme.Gray10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focus by remember { mutableStateOf(false) }
    TextButton(
        onClick = { onClick() },
        modifier = modifier
            .onFocusChanged { focus = it.hasFocus }
            .clip(RoundedCornerShape(30.dp))
            .background(
                color = if (focus) colorScheme.onBackground
                else colorScheme.background
            )
            .clickable(
                enabled = true,
                indication = null, interactionSource = null, onClick = { onClick() })
    ) {
        val fgColor = if (focus) colorScheme.background
        else colorScheme.onBackground
        Icon(
            Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null,
            tint = fgColor
        )
        Spacer(Modifier.size(10.dp))
        Text(
            "Go back",
            color = fgColor
        )
    }
}

@TvPreview
@Composable
private fun PreviewBackButton() {
    TripleRockTVTheme {
        BackButton {}
    }
}