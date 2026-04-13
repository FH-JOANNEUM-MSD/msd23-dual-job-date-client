package fh.msd.jobdating.feature.appointments.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingDto(
    val id: Int,
    @SerialName("slot_id") val slotId: Int,
    @SerialName("student_id") val studentId: Int,
    @SerialName("company_id") val companyId: Int
)