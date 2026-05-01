package com.joshgm3z.triplerocktv.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class DateUtilTest {
    private val now = System.currentTimeMillis() // May 1, 2026, 07:58:00 UTC
    private val oneDayAgo = now - (24 * 60 * 60 * 1000L)
    private val threeDaysAgo = now - (3 * 24 * 60 * 60 * 1000L)
    private val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000L)
    private val twoWeeksAgo = now - (14 * 24 * 60 * 60 * 1000L)
    private val oneMonthAgo = now - (30 * 24 * 60 * 60 * 1000L)
    private val twoMonthsAgo = now - (60 * 24 * 60 * 60 * 1000L)
    private val twoYearsAgo = now - (2 * 365 * 24 * 60 * 60 * 1000L)

    @Test
    fun relativeTime() {
        assertEquals("just now", now.relativeTime())
        assertEquals("1 day ago", oneDayAgo.relativeTime())
        assertEquals("3 days ago", threeDaysAgo.relativeTime())
        assertEquals("1 week ago", oneWeekAgo.relativeTime())
        assertEquals("2 weeks ago", twoWeeksAgo.relativeTime())
        assertEquals("1 month ago", oneMonthAgo.relativeTime())
        assertEquals("2 months ago", twoMonthsAgo.relativeTime())
        assertEquals("2 years ago", twoYearsAgo.relativeTime())
    }

}