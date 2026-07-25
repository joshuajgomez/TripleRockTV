package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.theme.Orange40

@Composable
fun MetadataBar(
    rating: Float? = null,
    favorite: Boolean = false,
    style: TextStyle = typography.labelLarge,
    list: List<String> = listOf()
) {
    val filteredList = list.filter { !it.trim().isEmpty() }
    val dot = "  •  "
    val color = colorScheme.primary
    val text = buildAnnotatedString {
        if (rating != null && rating > 0) {
            append(" $rating")
            if (filteredList.isNotEmpty()) append(dot)
        }
        filteredList.forEachIndexed { index, string ->
            append(string)
            if (index < filteredList.size - 1) append(dot)
        }
        if (favorite) append(dot)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (rating != null && rating > 0) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Orange40,
                modifier = Modifier.size(13.dp)
            )
        }
        Text(
            text = text,
            style = style,
            color = color
        )
        if (favorite) {
            Icon(
                Icons.AutoMirrored.Default.PlaylistAddCheck,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}