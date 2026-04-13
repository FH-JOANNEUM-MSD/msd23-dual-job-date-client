package fh.msd.jobdating.core.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreferenceDto(
    val id: Int,
    @SerialName("student_id") val studentId: Int,
    @SerialName("company_id") val companyId: Int,
    @SerialName("preference_type") val preferenceType: String
)