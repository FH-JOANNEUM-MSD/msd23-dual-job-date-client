package fh.msd.jobdating.feature.profile.ui

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isChangingPassword: Boolean = false,
    val passwordError: String? = null,
    val passwordSuccess: String? = null
)