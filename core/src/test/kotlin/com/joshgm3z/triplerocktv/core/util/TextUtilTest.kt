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
}