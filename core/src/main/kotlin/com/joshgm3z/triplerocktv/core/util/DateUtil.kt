package com.joshgm3z.triplerocktv.core.util

import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.relativeTime(now: Instant = Instant.now()): String {
    println("target time = $this")
    val target = Instant.ofEpochMilli(this)
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