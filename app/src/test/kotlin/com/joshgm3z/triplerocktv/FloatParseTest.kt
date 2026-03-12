package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.impl.helper.parseToFloat
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatParseTest {
    @Test
    fun test() {
        mapOf(
            null to 0f,
            "3.4" to 3.4f,
            "3.40" to 3.4f,
        ).forEach { (input, expected) ->
            val actual = input.parseToFloat()
            println("expected: $expected, actual: $actual")
            assertEquals(expected, actual)
        }
    }
}