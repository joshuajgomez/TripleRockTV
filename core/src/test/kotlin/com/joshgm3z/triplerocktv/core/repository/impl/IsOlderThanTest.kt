package com.joshgm3z.triplerocktv.core.repository.impl

import kotlin.test.Test

class IsOlderThanTest {

    @Test
    fun `Verify v2025_5_4 is older than v2025_5_5`() {
        assert("v2025.5.4".isOlderThan("v2025.5.5")) {
            "v2025.5.4 should be older than v2025.5.5"
        }
    }

    @Test
    fun `Verify v2025_5_4 is same as v2025_5_4`() {
        assert(!"v2025.5.4".isOlderThan("v2025.5.4")) {
            "Are same version"
        }
    }

    @Test
    fun `Verify v2025_5_4 is not older than v2025_5_3`() {
        assert(!"v2025.5.4".isOlderThan("v2025.5.3")) {
            "v2025.5.4 should not be older than v2025.5.3"
        }
    }

    @Test
    fun `Verify invalid versions return false`() {
        assert(!"1.0-core-default".isOlderThan("v2025.5.3")) {
            "Invalid version should not return false"
        }
    }
}