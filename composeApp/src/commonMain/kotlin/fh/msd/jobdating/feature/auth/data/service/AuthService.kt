package fh.msd.jobdating.feature.auth.data.service

import fh.msd.jobdating.core.domain.model.User
import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto

interface AuthService {
    suspend fun login(request: LoginRequestDto): String
    suspend fun logout()
    suspend fun getMe(token: String): User
    suspend fun tryRestoreSession(): String?
    suspend fun changePassword(newPassword: String)
    fun getCurrentUserEmail(): String?
}