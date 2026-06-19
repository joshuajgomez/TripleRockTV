package com.joshgm3z.triplerocktv.core.util

import com.joshgm3z.triplerocktv.core.BuildConfig
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

@Suppress("KotlinConstantConditions")
val isDevBuild
    get() = BuildConfig.FLAVOR != "online"

@Suppress("KotlinConstantConditions")
val isDemoBuild
    get() = BuildConfig.FLAVOR == "demo"