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

fun String.encodeWithSound(): String {
    if (isEmpty()) return ""
    var s = uppercase()

    // 1. Simplify Manglish common double-letters/sounds
    s = s.replace("ZH", "J")
    s = s.replace("TH", "T")
    s = s.replace("OO", "U")
    s = s.replace("EE", "I")
    s = s.replace("PH", "F")

    // 2. Apply basic Soundex-style mapping
    // (Keep first letter, map others to numbers, remove vowels)
    val firstLetter = s[0]
    val encoded = StringBuilder().append(firstLetter)

    val mapping = mapOf(
        'B' to '1',
        'F' to '1',
        'P' to '1',
        'V' to '1',
        'C' to '2',
        'G' to '2',
        'J' to '2',
        'K' to '2',
        'Q' to '2',
        'S' to '2',
        'X' to '2',
        'Z' to '2',
        'D' to '3',
        'T' to '3',
        'L' to '4',
        'M' to '5',
        'N' to '5',
        'R' to '6'
    )

    for (i in 1 until s.length) {
        val code = mapping[s[i]]
        if (code != null && code != encoded.last()) {
            encoded.append(code)
        }
    }

    return encoded.toString().padEnd(4, '0').substring(0, 4)
}