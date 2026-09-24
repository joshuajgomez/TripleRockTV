package com.joshgm3z.triplerocktv.core.util

import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.relativeTime(now: Instant = Instant.now()): String {
    // Heuristic: If the value is less than 100 billion, it's likely seconds.
    // (100 billion milliseconds is only ~3 years after 1970).
    val timestampMillis = if (this < 100_000_000_000L) this * 1000L else this
    val target = Instant.ofEpochMilli(timestampMillis)
    val duration = Duration.between(target, now)

    val seconds = duration.seconds
    return when {
        seconds < 60 -> "just now"
        seconds < 3600 -> {
            val mins = seconds / 60
            "${mins}m ago"
        }

        seconds < 86400 -> {
            val hours = seconds / 3600
            "${hours}h ago"
        }

        seconds < 604800 -> { // Less than 1 week
            val days = seconds / 86400
            "${days}d ago"
        }

        seconds < 2592000 -> { // Less than 30 days
            val weeks = seconds / 604800
            "${weeks}w ago"
        }

        seconds < 31536000 -> { // Less than 1 year (365 days)
            val months = seconds / 2592000
            if (months == 1L) "1 month ago" else "$months months ago"
        }

        else -> {
            val years = seconds / 31536000
            "${years}y ago"
        }
    }
}

fun getTimeFrames(): List<ZonedDateTime> {
    val now = ZonedDateTime.now()

    // 1. Round down to the nearest 30-minute block
    val minutes = now.minute
    val roundedNow = if (minutes < 30) {
        now.withMinute(0).withSecond(0).withNano(0)
    } else {
        now.withMinute(30).withSecond(0).withNano(0)
    }

    // 2. Start from 30 minutes before the rounded current time
    val startTime = roundedNow.minusMinutes(30)

    // 3. Generate a list (e.g., for the next 24 hours in 30-min increments)
    return (0 until 7).map { i ->
        startTime.plusMinutes(i * 30L)
    }
}

fun ZonedDateTime.toTextTime(format: String = "EEE, MMM dd, hh:mm a"): String {
    val formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
    return format(formatter)
}

fun Long.isRecent(days: Long = 5): Boolean {
    val threeDaysInMillis = TimeUnit.DAYS.toMillis(days)
    val now = System.currentTimeMillis()

    // Normalize to milliseconds
    val iptvTimeMillis = this * 1000L
    val diff = now - iptvTimeMillis

    return diff in 0..threeDaysInMillis
}