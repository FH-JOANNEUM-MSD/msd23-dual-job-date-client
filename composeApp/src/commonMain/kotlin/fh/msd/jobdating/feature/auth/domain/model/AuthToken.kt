package fh.msd.jobdating.feature.auth.domain.model

data class AuthToken(
    val token: String,
    val userId: String
)