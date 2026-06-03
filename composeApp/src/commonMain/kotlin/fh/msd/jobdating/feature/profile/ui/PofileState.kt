package fh.msd.jobdating.feature.profile.ui

sealed class ProfilePasswordError {
    data object FieldsRequired : ProfilePasswordError()
    data object PasswordMismatch : ProfilePasswordError()
    data object TooShort : ProfilePasswordError()
    data object ChangeFailed : ProfilePasswordError()
}

data class ProfileState(
    val email: String = "",
    val userId: String = "",
    val studentId: String = "",
    val role: String = "",
    val isLoading: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isChangingPassword: Boolean = false,
    val passwordError: ProfilePasswordError? = null,
    val passwordSuccess: Boolean = false
)
