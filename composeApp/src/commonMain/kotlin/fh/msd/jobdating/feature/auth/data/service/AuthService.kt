
package fh.msd.jobdating.feature.auth.data.service

import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import fh.msd.jobdating.feature.auth.domain.dto.LoginResponseDto

interface AuthService {
    suspend fun login(request: LoginRequestDto): LoginResponseDto
    suspend fun logout()
}