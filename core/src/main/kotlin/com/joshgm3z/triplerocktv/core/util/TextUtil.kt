package com.joshgm3z.triplerocktv.core.util

import java.text.NumberFormat

fun Int.withComma(): String = try {
    NumberFormat.getInstance().format(this)
} catch (e: Exception) {
    e.printStackTrace()
    this.toString()
}

fun String.parseEpisodeNumber(givenEpisodeNum: Int): Int {
    val regex = """[Ee](\d+)""".toRegex()
    val matchResult = regex.find(this)
    return if (matchResult != null) {
        matchResult.groupValues[1].toIntOrNull() ?: givenEpisodeNum
    } else {
        givenEpisodeNum
    }
}

fun Int.asTwoDigit(): String = String.format("%02d", this)

fun Long.toTextTime(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m")
    }.trim()
}