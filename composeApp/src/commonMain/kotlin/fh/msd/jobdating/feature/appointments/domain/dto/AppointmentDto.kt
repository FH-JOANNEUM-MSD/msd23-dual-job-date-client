package fh.msd.jobdating.feature.appointments.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    val id: String,
    val companyId: String,
    val companyName: String,
    val timeSlot: String
)

