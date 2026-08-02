package com.joshgm3z.triplerocktv.compose.screens.browse.uistate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.theme.subTextColor

@Composable
fun SectionTitle(
    title: String,
    showLoading: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorScheme.background)
            .padding(paddingValues = paddingValues),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = typography.titleSmall,
            color = subTextColor(),
            modifier = Modifier
                .padding(
                    start = 5.dp, end = 5.dp,
                    top = 12.dp, bottom = 5.dp
                )
        )
        AnimatedVisibility(showLoading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp))
        }
    }
}