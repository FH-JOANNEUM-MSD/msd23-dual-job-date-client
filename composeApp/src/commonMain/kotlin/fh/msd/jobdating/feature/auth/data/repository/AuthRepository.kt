package fh.msd.jobdating.feature.auth.data.repository

interface AuthRepository {
    suspend fun login(email: String, password: String)
    suspend fun logout()
}