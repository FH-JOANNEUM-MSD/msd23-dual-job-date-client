package fh.msd.jobdating.feature.auth.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val studentId: String
)

@Serializable
data class MeResponseDto(
    @SerialName("user_id") val userId: String,
    val status: String
)