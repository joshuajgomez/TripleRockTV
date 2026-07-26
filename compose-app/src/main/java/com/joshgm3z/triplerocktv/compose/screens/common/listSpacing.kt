package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.screens.settings.appBottomPadding

fun LazyListScope.listSpacing(size: Dp = 50.dp) = item {
    Spacer(Modifier.size(size))
}

fun LazyGridScope.gridSpacing(size: Dp = 50.dp) = item(
    span = { GridItemSpan(maxLineSpan) }
) {
    Spacer(Modifier.size(size))
}
