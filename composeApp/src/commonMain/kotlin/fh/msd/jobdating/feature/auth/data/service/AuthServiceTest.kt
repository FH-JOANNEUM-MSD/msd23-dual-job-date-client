
package fh.msd.jobdating.feature.auth.data.service

import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import fh.msd.jobdating.feature.auth.domain.dto.LoginResponseDto

class AuthServiceTest : AuthService {

    override suspend fun login(request: LoginRequestDto): LoginResponseDto {
        // accepts any credentials
        return LoginResponseDto(
            token = "test-token-123",
            studentId = "student-1"
        )
    }

    override suspend fun logout() {
        // stub
    }
}