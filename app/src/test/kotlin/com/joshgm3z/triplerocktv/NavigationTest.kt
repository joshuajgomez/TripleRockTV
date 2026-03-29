package com.joshgm3z.triplerocktv

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class NavigationTest {

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Test
    fun `Verify startDestination is SplashScreenFragment`() {
        navController.setGraph(R.navigation.nav_graph)

        assertEquals(
            expected = R.id.splashScreenFragment,
            actual = navController.graph.startDestinationId,
            "Start destination should be SplashScreenFragment"
        )
    }
}
