package fh.msd.jobdating.core.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto(
    val id: Int,
    @SerialName("user_id") val userId: String,
    @SerialName("study_program") val studyProgram: String?,
    val semester: Int?,
    @SerialName("first_name") val firstName: String?,
    @SerialName("last_name") val lastName: String?
)