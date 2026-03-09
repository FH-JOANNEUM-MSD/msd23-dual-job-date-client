package fh.msd.jobdating.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.feature.profile.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileNavigation {
    data object ToLogin : ProfileNavigation()
}

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<ProfileNavigation>()
    val navigation = _navigation.asSharedFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.CurrentPasswordChanged -> {
                _state.value = _state.value.copy(currentPassword = event.value)
            }
            is ProfileEvent.NewPasswordChanged -> {
                _state.value = _state.value.copy(newPassword = event.value)
            }
            is ProfileEvent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(confirmPassword = event.value)
            }
            is ProfileEvent.ChangePassword -> changePassword()
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getProfile().fold(
                onSuccess = { profile ->
                    _state.value = _state.value.copy(
                        name = profile.name,
                        email = profile.email,
                        isLoading = false
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun changePassword() {
        val currentState = _state.value

        if (currentState.currentPassword.isBlank() ||
            currentState.newPassword.isBlank() ||
            currentState.confirmPassword.isBlank()
        ) {
            _state.value = _state.value.copy(
                passwordError = "All fields are required",
                passwordSuccess = null
            )
            return
        }

        if (currentState.newPassword != currentState.confirmPassword) {
            _state.value = _state.value.copy(
                passwordError = "Passwords do not match",
                passwordSuccess = null
            )
            return
        }

        if (currentState.newPassword.length < 6) {
            _state.value = _state.value.copy(
                passwordError = "Password must be at least 6 characters",
                passwordSuccess = null
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isChangingPassword = true,
                passwordError = null,
                passwordSuccess = null
            )

            repository.changePassword(
                currentPassword = currentState.currentPassword,
                newPassword = currentState.newPassword
            ).fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isChangingPassword = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        passwordError = null,
                        passwordSuccess = "Password changed successfully"
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isChangingPassword = false,
                        passwordError = error.message ?: "Failed to change password",
                        passwordSuccess = null
                    )
                }
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            repository.logout()
            _navigation.emit(ProfileNavigation.ToLogin)
        }
    }
}