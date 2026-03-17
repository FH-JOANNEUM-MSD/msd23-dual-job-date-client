package fh.msd.jobdating.feature.auth.data.repository

import fh.msd.jobdating.feature.auth.data.service.AuthService
import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import fh.msd.jobdating.feature.auth.domain.model.AuthToken

class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthToken {
        val token = authService.login(LoginRequestDto(email = email, password = password))
        val me = authService.getMe(token)
        return AuthToken(
            token = token,
            userId = me.userId
        )
    }

    override suspend fun logout() {
        authService.logout()
    }
}