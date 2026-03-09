package fh.msd.jobdating.feature.auth.data.repository

import fh.msd.jobdating.feature.auth.data.service.AuthService
import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import fh.msd.jobdating.feature.auth.domain.model.AuthToken

class AuthRepositoryTest(
    private val service: AuthService
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthToken {
        val response = service.login(LoginRequestDto(email, password))
        return AuthToken(
            token = response.token,
            studentId = response.studentId
        )
    }

    override suspend fun logout() = service.logout()
}