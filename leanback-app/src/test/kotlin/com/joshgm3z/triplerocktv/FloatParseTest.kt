package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatParseTest {
    @Test
    fun test() {
        mapOf(
            null to 0f,
            "3.4" to 3.4f,
            "3.405" to 3.4f,
            "3.4999999995" to 3.5f,
        ).forEach { (input, expected) ->
            val actual = input.parseToFloat()
            println("expected: $expected, actual: $actual")
            assertEquals(expected, actual)
        }
    }
}