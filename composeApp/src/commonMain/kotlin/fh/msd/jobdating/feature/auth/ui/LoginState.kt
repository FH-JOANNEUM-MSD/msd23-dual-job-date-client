
package fh.msd.jobdating.feature.auth.ui

data class LoginState(
    val email: String = "arya.stark@westeros.com",
    val password: String = "WinterIsComing123!",
    val isLoading: Boolean = false,
    val error: String? = null
)
