package fh.msd.jobdating.core.startup

import fh.msd.jobdating.feature.auth.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_sessionRestored_finishesLoadingAndIsLoggedIn() = runTest {
        val vm = StartupViewModel(FakeAuthRepository(restored = true))

        assertFalse(vm.state.value.isLoading)
        assertTrue(vm.state.value.isLoggedIn)
    }

    @Test
    fun init_noSession_finishesLoadingAndNotLoggedIn() = runTest {
        val vm = StartupViewModel(FakeAuthRepository(restored = false))

        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.state.value.isLoggedIn)
    }

    @Test
    fun init_restoreThrows_finishesLoadingAndNotLoggedIn() = runTest {
        val vm = StartupViewModel(FakeAuthRepository(throwOnRestore = true))

        // A failed restore must not hang the splash or crash — fall back to login.
        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.state.value.isLoggedIn)
    }

    @Test
    fun init_callsRestoreSessionExactlyOnce() = runTest {
        val repo = FakeAuthRepository(restored = true)

        StartupViewModel(repo)

        assertEquals(1, repo.restoreCount)
    }

    private class FakeAuthRepository(
        private val restored: Boolean = false,
        private val throwOnRestore: Boolean = false
    ) : AuthRepository {
        var restoreCount = 0
            private set

        override suspend fun restoreSession(): Boolean {
            restoreCount++
            if (throwOnRestore) throw RuntimeException("restore failed")
            return restored
        }

        override suspend fun login(email: String, password: String) = Unit
        override suspend fun logout() = Unit
        override suspend fun changePassword(newPassword: String) = Unit
        override fun getCurrentUserEmail(): String? = null
    }
}
