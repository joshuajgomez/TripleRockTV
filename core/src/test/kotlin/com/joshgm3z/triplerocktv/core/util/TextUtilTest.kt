package com.joshgm3z.triplerocktv.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class TextUtilTest {
    @Test
    fun test_parseEpisodeNumber_285() {
        val text = "THE KAPIL SHARMA SHOW NEW SEASON  - S03E285 - "
        assertEquals(285, text.parseEpisodeNumber(96))
    }

    @Test
    fun test_parseEpisodeNumber_02() {
        val text = "THE KAPIL SHARMA SHOW NEW SEASON  - S03E02 - "
        assertEquals(2, text.parseEpisodeNumber(3))
    }

    @Test
    fun test_getVersionCode() {
        assertEquals(234, "v234".getVersionCode())
        assertEquals(234, "dev-v234".getVersionCode())
        assertEquals(234, "dev-v234".getVersionCode())
        assertEquals(0, "v".getVersionCode())
    }

    @Test
    fun test_secondsToTextTime() {
        assertEquals("1s", 1L.secondsToTextTime())
        assertEquals("10s", 10L.secondsToTextTime())
        assertEquals("1m 0s", 60L.secondsToTextTime())
        assertEquals("2m 0s", 120L.secondsToTextTime())
        assertEquals("1h 0m 0s", 3600L.secondsToTextTime())
        assertEquals("1h 1m 0s", 3660L.secondsToTextTime())
        assertEquals("16m 40s", 1000L.secondsToTextTime())
        assertEquals("2h 46m 40s", 10000L.secondsToTextTime())
    }

}