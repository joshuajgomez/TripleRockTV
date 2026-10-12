package com.joshgm3z.triplerocktv.core.repository.impl

import kotlin.test.Test
import kotlin.test.assertEquals


class GetNameTest {

    @Test
    fun `test getName with valid URL`() {
        assertEquals(
            "iptv-org.github.io",
            "https://iptv-org.github.io/iptv/index.m3u".getName()
        )
    }
}
