package fh.msd.jobdating.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            is LoginEvent.EmailChanged -> _state.update { it.copy(email = event.value) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = event.value) }
            is LoginEvent.Submit -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                repository.login(_state.value.email, _state.value.password)
                _state.update { it.copy(isLoading = false) }
                _navigation.emit(LoginNavigation.ToCompanies)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}