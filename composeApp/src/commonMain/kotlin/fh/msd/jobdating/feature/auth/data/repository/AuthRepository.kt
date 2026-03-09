package fh.msd.jobdating.feature.auth.data.repository

import fh.msd.jobdating.feature.auth.domain.model.AuthToken

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthToken
    suspend fun logout()
}
