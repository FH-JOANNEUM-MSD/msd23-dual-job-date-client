package fh.msd.jobdating.feature.auth.ui

import fh.msd.jobdating.core.AppConfig

sealed class LoginError {
    data object Network : LoginError()
    data object InvalidCredentials : LoginError()
    data object Generic : LoginError()
}

data class LoginState(
    val email: String = if (AppConfig.isProduction) "" else "robb.stark@westeros.com",
    val password: String = if (AppConfig.isProduction) "" else "WinterIsComing123!",
    val isLoading: Boolean = false,
    val loginError: LoginError? = null
)
