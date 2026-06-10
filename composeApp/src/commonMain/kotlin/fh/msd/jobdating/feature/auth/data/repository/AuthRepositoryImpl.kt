package fh.msd.jobdating.feature.auth.data.repository

import fh.msd.jobdating.core.domain.model.User
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

    override suspend fun restoreSession(): Boolean {
        // tryRestoreSession() awaits Supabase's load-from-storage, so the token
        // (refreshed if needed) is settled before we decide where to route.
        val token = authService.tryRestoreSession() ?: return false

        // We have a valid token. Make sure the local user (studentId etc.) is
        // present too; if it was lost, re-fetch it so the app has what it needs.
        if (userSession.getUser() == null) {
            return try {
                userSession.setUser(authService.getMe(token))
                true
            } catch (e: Exception) {
                false
            }
        }
        return true
    }

    override suspend fun logout() {
        authService.logout()
        userSession.clear()
    }

    override suspend fun changePassword(newPassword: String) {
        authService.changePassword(newPassword)
    }

    override fun getCurrentUserEmail(): String? {
        return authService.getCurrentUserEmail()
    }
}