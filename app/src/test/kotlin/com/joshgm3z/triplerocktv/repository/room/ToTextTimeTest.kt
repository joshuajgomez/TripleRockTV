package com.joshgm3z.triplerocktv.repository.room

import kotlin.test.Test
import kotlin.test.assertEquals

class ToTextTimeTest {

    @Test
    fun test() {
        assertEquals("1h 23m", 4980000L.toTextTime())
        assertEquals("", 0L.toTextTime())
        assertEquals("45m", 2700000L.toTextTime())
    }
}