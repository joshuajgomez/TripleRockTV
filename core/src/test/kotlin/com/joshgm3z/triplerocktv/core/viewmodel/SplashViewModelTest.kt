package com.joshgm3z.triplerocktv.core.viewmodel

import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.core.repository.AccessState
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val localDatastore: LocalDatastore = mockk()
    private val repository: MediaLocalRepository = mockk()
    private val accessControlRepository: AccessControlRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

//    @Test TODO: Fix this test
    fun `init sets AccessDisabled when access state is disabled`() = runTest {
        coEvery { localDatastore.getUserInfo() } returns null
        coEvery {
            accessControlRepository.getAccessState(any())
        } returns AccessState(enabled = false, reason = "Account Suspended")
        coEvery { accessControlRepository.appUpdateState() } returns mockk()

        viewModel = SplashViewModel(localDatastore, repository, accessControlRepository)
        // Act
        advanceUntilIdle() // Wait for init block coroutine to finish

        // Assert
        viewModel.navDirectionState.collectLatest {
            assertIs<DestinationState.AccessDisabled>(it)
            assertEquals("Account Suspended", it.message)
        }
    }

}