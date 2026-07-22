package com.joshgm3z.triplerocktv.compose.screens.common

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = ">",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
annotation class DarkPreview

@Preview(
    name = ">",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
annotation class DarkLandscapePreview