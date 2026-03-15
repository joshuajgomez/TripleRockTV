package com.joshgm3z.triplerocktv.ui.player

import kotlinx.coroutines.delay

class PeriodicReminderUtility {
    suspend fun getPeriodicReminder(
        period: Long = 30000L, // 10 seconds
        onRemind: () -> Unit
    ) {
        while (true) {
            delay(period)
            onRemind()
        }
    }
}