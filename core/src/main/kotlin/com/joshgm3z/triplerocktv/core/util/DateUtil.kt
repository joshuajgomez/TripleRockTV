package com.joshgm3z.triplerocktv.core.util

import java.time.Duration
import java.time.Instant

fun Long.relativeTime(now: Instant = Instant.now()): String {
    println("target time = $this")
    val target = Instant.ofEpochMilli(this)
    val duration = Duration.between(target, now)

    val seconds = duration.seconds
    return when {
        seconds < 60 -> "just now"
        seconds < 3600 -> {
            val mins = seconds / 60
            if (mins == 1L) "1 minute ago" else "$mins minutes ago"
        }

        seconds < 86400 -> {
            val hours = seconds / 3600
            if (hours == 1L) "1 hour ago" else "$hours hours ago"
        }

        seconds < 604800 -> { // Less than 1 week
            val days = seconds / 86400
            if (days == 1L) "1 day ago" else "$days days ago"
        }

        seconds < 2592000 -> { // Less than 30 days
            val weeks = seconds / 604800
            if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
        }

        seconds < 31536000 -> { // Less than 1 year (365 days)
            val months = seconds / 2592000
            if (months == 1L) "1 month ago" else "$months months ago"
        }

        else -> {
            val years = seconds / 31536000
            if (years == 1L) "1 year ago" else "$years years ago"
        }
    }
}