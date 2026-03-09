package fh.msd.jobdating.feature.profile.ui

sealed class ProfileEvent {
    data class CurrentPasswordChanged(val value: String) : ProfileEvent()
    data class NewPasswordChanged(val value: String) : ProfileEvent()
    data class ConfirmPasswordChanged(val value: String) : ProfileEvent()
    data object ChangePassword : ProfileEvent()
    data object Logout : ProfileEvent()
}