package fh.msd.jobdating.feature.profile.data.repository

import fh.msd.jobdating.feature.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun logout()
}