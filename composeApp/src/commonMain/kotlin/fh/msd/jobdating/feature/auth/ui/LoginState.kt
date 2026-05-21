
package fh.msd.jobdating.feature.auth.ui

data class LoginState(
    val email: String = "ned.stark@winterfell.net",
    val password: String = "WinterIsComing123!",
    val isLoading: Boolean = false,
    val error: String? = null
)
