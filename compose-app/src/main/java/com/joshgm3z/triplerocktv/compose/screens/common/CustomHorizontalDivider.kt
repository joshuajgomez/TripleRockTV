package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun CustomHorizontalDivider(index: Int, size: Int) {
    if (index < size - 1) HorizontalDivider(modifier = Modifier.alpha(0.5f))
}