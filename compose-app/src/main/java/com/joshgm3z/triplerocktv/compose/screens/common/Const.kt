package com.joshgm3z.triplerocktv.compose.screens.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val appHorizontalPadding = 20.dp
fun appBottomPadding() = 40.dp

@Composable
fun appTopPadding() = if (isLandscape()) 30.dp else 70.dp