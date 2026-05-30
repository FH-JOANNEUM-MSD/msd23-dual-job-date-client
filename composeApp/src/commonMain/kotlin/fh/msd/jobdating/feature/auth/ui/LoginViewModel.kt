package fh.msd.jobdating.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.core.network.isNetworkError
import fh.msd.jobdating.feature.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LoginNavigation {
    data object ToCompanies : LoginNavigation()
}

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<LoginNavigation>()
    val navigation = _navigation.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _state.update {
                it.copy(email = event.value, loginError = null)
            }
            is LoginEvent.PasswordChanged -> _state.update {
                it.copy(password = event.value, loginError = null)
            }
            is LoginEvent.Submit -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loginError = null) }
            try {
                repository.login(_state.value.email, _state.value.password)
                _state.update { it.copy(isLoading = false) }
                _navigation.emit(LoginNavigation.ToCompanies)
            } catch (e: Exception) {
                val error = when {
                    e.isNetworkError() -> LoginError.Network
                    e.message?.contains("Invalid login credentials", ignoreCase = true) == true -> LoginError.InvalidCredentials
                    e.message?.contains("invalid_credentials", ignoreCase = true) == true -> LoginError.InvalidCredentials
                    else -> LoginError.Generic
                }
                _state.update { it.copy(isLoading = false, loginError = error) }
            }
        }
    }
}
