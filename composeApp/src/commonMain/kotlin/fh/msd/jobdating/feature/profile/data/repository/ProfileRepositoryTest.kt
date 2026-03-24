package fh.msd.jobdating.feature.profile.data.repository

import fh.msd.jobdating.feature.profile.domain.model.Profile
import kotlinx.coroutines.delay

class ProfileRepositoryTest : ProfileRepository {
    override suspend fun getProfile(): Result<Profile> {
        delay(500)
        return Result.success(
            Profile(
                id = "student-1",
                name = "Max Mustermann",
                email = "max.mustermann@student.fh.at"
            )
        )
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        delay(1000)
        return if (currentPassword == "wrong") {
            Result.failure(Exception("Current password is incorrect"))
        } else {
            Result.success(Unit)
        }
    }

}