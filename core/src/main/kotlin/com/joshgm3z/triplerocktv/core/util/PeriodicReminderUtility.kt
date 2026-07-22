package com.joshgm3z.triplerocktv.core.util

import kotlinx.coroutines.delay

suspend fun remindPeriodically(
    period: Long = 30000L, // 10 seconds
    onRemind: () -> Unit
) {
    while (true) {
        onRemind()
        delay(period)
    }
}