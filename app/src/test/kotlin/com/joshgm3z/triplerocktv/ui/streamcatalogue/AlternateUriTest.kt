package com.joshgm3z.triplerocktv.ui.streamcatalogue

import kotlin.test.Test
import kotlin.test.assertEquals

class AlternateUriTest {

    private val sampleServerUrl = "http://someserver.abc:2000"

    @Test
    fun test_non_null_serverUrl() {
        val input = "http://starshare.live:8080/images/6ffb68625b168d5a5618f7ea2e0a1035.jpg"
        assertEquals(
            "$sampleServerUrl/images/6ffb68625b168d5a5618f7ea2e0a1035.jpg",
            input.alternateUri(sampleServerUrl)
        )
    }

    @Test
    fun test_null_Uri() {
        assertEquals(
            null,
            null.alternateUri(sampleServerUrl)
        )
    }
}