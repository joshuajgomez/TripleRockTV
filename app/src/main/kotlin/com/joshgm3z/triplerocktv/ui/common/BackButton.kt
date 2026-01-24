package com.joshgm3z.triplerocktv.ui.common

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.triplerocktv.ui.theme.Gray10

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = { onClick() },
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color = Gray10)
            .padding(horizontal = 5.dp)
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