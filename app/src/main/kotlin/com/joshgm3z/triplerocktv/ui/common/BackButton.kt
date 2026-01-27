package com.joshgm3z.triplerocktv.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
    ) {
        Icon(
            Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null,
        )
        Spacer(Modifier.size(5.dp))
        Text("Go back")
    }
}

@TvPreview
@Composable
private fun PreviewBackButton() {
    TripleRockTVTheme {
        BackButton {}
    }
}