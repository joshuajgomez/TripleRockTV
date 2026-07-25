package com.joshgm3z.triplerocktv.core.util

import com.google.firebase.firestore.model.Values.timestamp
import com.joshgm3z.triplerocktv.core.BuildConfig
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.format
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.util.Date

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

fun String?.ifNullOrEmpty(defaultValue: String?) =
    if (isNullOrEmpty()) defaultValue else this

@Suppress("KotlinConstantConditions")
val isDevBuild
    get() = BuildConfig.FLAVOR != "online"

fun String.orIfDebug(secretText: String) = if (isDevBuild) secretText else this

@Suppress("KotlinConstantConditions")
val isDemoBuild
    get() = BuildConfig.FLAVOR == "demo"

fun String.getVersionCode(): Int = replace(Regex("[^0-9]"), "")
    .toIntOrNull() ?: 0

@OptIn(ExperimentalTime::class)
fun String?.formatTimestamp(): String {
    if (this == null) return ""
    return try {
        val instant = Instant.ofEpochSecond(this.toLong())
        val formatter = DateTimeFormatter.ofPattern("HH:mm") // Use "hh:mm a" for 12-hour format
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        ""
    }
}

fun String?.formatExpiryDate(): String {
    return try {
        if (this == null) return "Unknown"
        // Convert seconds to milliseconds
        val date = Date(this.toLong() * 1000)
        val format = SimpleDateFormat("MMM dd, yyyy")
        format.format(date)
    } catch (e: Exception) {
        "Unknown"
    }
}