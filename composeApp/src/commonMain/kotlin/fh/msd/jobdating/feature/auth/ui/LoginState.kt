
package fh.msd.jobdating.feature.auth.ui

data class LoginState(
    val email: String = "sansa.stark@westeros.com",
    val password: String = "12345678",
    val isLoading: Boolean = false,
    val error: String? = null
)
