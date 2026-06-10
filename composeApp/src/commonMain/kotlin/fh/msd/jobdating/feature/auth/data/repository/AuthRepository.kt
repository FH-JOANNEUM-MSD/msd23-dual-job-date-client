package fh.msd.jobdating.feature.auth.data.repository

interface AuthRepository {
    suspend fun login(email: String, password: String)

    /**
     * Restores a previously persisted session on app start.
     *
     * Returns true only when there is a usable session (a valid Supabase token
     * and a local user). Suspends until Supabase has finished loading from
     * storage, so callers can safely route to authenticated screens afterwards
     * without racing the token load.
     */
    suspend fun restoreSession(): Boolean

    suspend fun logout()
    suspend fun changePassword(newPassword: String)
    fun getCurrentUserEmail(): String?
}