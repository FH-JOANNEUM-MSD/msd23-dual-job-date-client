package fh.msd.jobdating.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.core.session.UserSession
import fh.msd.jobdating.feature.auth.data.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ProfileNavigation {
    data object ToLogin : ProfileNavigation()
}

class ProfileViewModel(
    private val userSession: UserSession,
    private val authRepository: AuthRepository,
    private val supabaseClient: SupabaseClient
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
                _state.update { it.copy(currentPassword = event.value) }
            }
            is ProfileEvent.NewPasswordChanged -> {
                _state.update { it.copy(newPassword = event.value) }
            }
            is ProfileEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.value) }
            }
            is ProfileEvent.ChangePassword -> changePassword()
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        val user = userSession.getUser()
        if (user != null) {
            _state.update {
                it.copy(
                    userId = user.userId,
                    studentId = user.studentId.toString(),
                    role = user.role.replaceFirstChar { it.uppercase() },
                    isLoading = false
                )
            }
        }
    }

    private fun changePassword() {
        val currentState = _state.value

        if (currentState.newPassword.isBlank() || currentState.confirmPassword.isBlank()) {
            _state.update {
                it.copy(
                    passwordError = "All fields are required",
                    passwordSuccess = null
                )
            }
            return
        }

        if (currentState.newPassword != currentState.confirmPassword) {
            _state.update {
                it.copy(
                    passwordError = "Passwords do not match",
                    passwordSuccess = null
                )
            }
            return
        }

        if (currentState.newPassword.length < 6) {
            _state.update {
                it.copy(
                    passwordError = "Password must be at least 6 characters",
                    passwordSuccess = null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isChangingPassword = true,
                    passwordError = null,
                    passwordSuccess = null
                )
            }

            try {
                supabaseClient.auth.updateUser {
                    password = currentState.newPassword
                }

                _state.update {
                    it.copy(
                        isChangingPassword = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        passwordError = null,
                        passwordSuccess = "Password changed successfully"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isChangingPassword = false,
                        passwordError = e.message ?: "Failed to change password",
                        passwordSuccess = null
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _navigation.emit(ProfileNavigation.ToLogin)
        }
    }
}