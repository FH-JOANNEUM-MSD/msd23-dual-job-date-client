package fh.msd.jobdating.feature.appointments.domain.model

data class Appointment(
    val id: String,
    val companyId: String,
    val companyName: String,
    val timeSlot: String
)
