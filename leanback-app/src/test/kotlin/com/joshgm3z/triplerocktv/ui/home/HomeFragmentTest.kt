package com.joshgm3z.triplerocktv.ui.home

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.joshgm3z.triplerocktv.MainActivity
import com.joshgm3z.triplerocktv.core.viewmodel.HomeViewModel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [34])
class HomeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Automatically replaces the real ViewModel in the Hilt graph
    @BindValue
    val viewModel: HomeViewModel = mockk(relaxed = true)

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun init() {
        hiltRule.inject()
        // Link the mock flow to the ViewModel property
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun homeFragment_whenDataLoaded_showsHomeCards() = runTest {
        activityScenario.onActivity { activity ->
            val navController = activity.findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.homeFragment)
            advanceUntilIdle()

            onView(withText("Video on demand")).check(matches(isDisplayed()))
        }

    }
}