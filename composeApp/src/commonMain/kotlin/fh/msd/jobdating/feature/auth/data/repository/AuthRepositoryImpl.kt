package fh.msd.jobdating.feature.auth.data.repository

import fh.msd.jobdating.core.session.UserSession
import fh.msd.jobdating.feature.auth.data.service.AuthService
import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val userSession: UserSession
) : AuthRepository {

    override suspend fun login(email: String, password: String) {
        val token = authService.login(LoginRequestDto(email, password))
        val user = authService.getMe(token)
        userSession.setUser(user)
    }

    override suspend fun logout() {
        authService.logout()
        userSession.clear()
    }
}