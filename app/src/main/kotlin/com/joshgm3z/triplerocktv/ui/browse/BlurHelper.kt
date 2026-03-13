package com.joshgm3z.triplerocktv.ui.browse

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

val colorFilter = PorterDuffColorFilter(
    Color.argb(
        180,
        0,
        0,
        0
    ), // 180 is the darkness (0-255). Increase for darker, decrease for lighter.
    PorterDuff.Mode.SRC_ATOP
)