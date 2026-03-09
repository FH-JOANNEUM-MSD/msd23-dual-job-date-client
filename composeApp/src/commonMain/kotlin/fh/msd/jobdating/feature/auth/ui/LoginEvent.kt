package fh.msd.jobdating.feature.auth.ui

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    data object Submit : LoginEvent()
}
