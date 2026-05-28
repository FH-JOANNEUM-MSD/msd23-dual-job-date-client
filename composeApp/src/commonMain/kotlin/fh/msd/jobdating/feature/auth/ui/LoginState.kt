
package fh.msd.jobdating.feature.auth.ui

data class LoginState(
    val email: String = "robb.stark@westeros.com",
    val password: String = "WinterIsComing123!",
    val isLoading: Boolean = false,
    val error: String? = null
)
