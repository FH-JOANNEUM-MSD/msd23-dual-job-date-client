
package fh.msd.jobdating.feature.auth.ui

import fh.msd.jobdating.core.AppConfig

data class LoginState(
    val email: String = if (AppConfig.isProduction) "" else "robb.stark@westeros.com",
    val password: String = if (AppConfig.isProduction) "" else "WinterIsComing123!",
    val isLoading: Boolean = false,
    val error: String? = null
)
