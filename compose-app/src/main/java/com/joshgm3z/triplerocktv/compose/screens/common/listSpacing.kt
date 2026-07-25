package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun LazyListScope.listSpacing(size: Dp = 50.dp) = item {
    Spacer(Modifier.size(size))
}