package fh.msd.jobdating.feature.auth.ui

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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun emailChanged_updatesEmailAndClearsError() = runTest {
        val vm = LoginViewModel(FakeAuthRepository())

        vm.onEvent(LoginEvent.EmailChanged("alice@example.com"))

        assertEquals("alice@example.com", vm.state.value.email)
        assertNull(vm.state.value.loginError)
    }

    @Test
    fun submit_successfulLogin_clearsLoadingAndError() = runTest {
        val vm = LoginViewModel(FakeAuthRepository(shouldFail = false))

        vm.onEvent(LoginEvent.Submit)

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.loginError)
    }

    @Test
    fun submit_invalidCredentials_setsInvalidCredentialsError() = runTest {
        val vm = LoginViewModel(
            FakeAuthRepository(
                shouldFail = true,
                errorMessage = "Invalid login credentials"
            )
        )

        vm.onEvent(LoginEvent.Submit)

        assertEquals(LoginError.InvalidCredentials, vm.state.value.loginError)
        assertFalse(vm.state.value.isLoading)
    }

    private class FakeAuthRepository(
        private val shouldFail: Boolean = false,
        private val errorMessage: String = "boom"
    ) : AuthRepository {
        override suspend fun login(email: String, password: String) {
            if (shouldFail) throw RuntimeException(errorMessage)
        }
        override suspend fun logout() = Unit
        override suspend fun changePassword(newPassword: String) = Unit
        override fun getCurrentUserEmail(): String? = null
    }
}
