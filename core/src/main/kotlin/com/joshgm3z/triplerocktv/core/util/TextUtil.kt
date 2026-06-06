package com.joshgm3z.triplerocktv.core.util

import java.text.NumberFormat

fun Int.withComma(): String = try {
    NumberFormat.getInstance().format(this)
} catch (e: Exception) {
    e.printStackTrace()
    this.toString()
}
