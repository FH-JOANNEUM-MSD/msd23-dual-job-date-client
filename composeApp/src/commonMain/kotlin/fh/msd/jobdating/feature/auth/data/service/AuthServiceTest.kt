package fh.msd.jobdating.feature.auth.data.service

import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import fh.msd.jobdating.feature.auth.domain.dto.MeResponseDto

class AuthServiceTest : AuthService {

    override suspend fun login(request: LoginRequestDto): String {
        // accepts any credentials
        return "Ok"
    }

    override suspend fun logout() {
        // stub
    }

    override suspend fun getMe(token: String): MeResponseDto {
        return MeResponseDto(
            userId = "1",
            status = "ok"
        )
    }

    override suspend fun tryRestoreSession(): String? = null
}