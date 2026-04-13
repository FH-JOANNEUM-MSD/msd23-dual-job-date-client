package fh.msd.jobdating.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id") val userId: String,
    val status: String,
    val role: String,
    @SerialName("student_id") val studentId: Int
)