package fh.msd.jobdating.feature.auth.data.service

import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import fh.msd.jobdating.feature.auth.domain.dto.MeResponseDto

interface AuthService {
    suspend fun login(request: LoginRequestDto): String
    suspend fun logout()
    suspend fun getMe(token: String): MeResponseDto
    suspend fun tryRestoreSession(): String?
}