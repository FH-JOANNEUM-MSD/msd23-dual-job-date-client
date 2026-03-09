package fh.msd.jobdating.feature.appointments.ui

import fh.msd.jobdating.feature.appointments.domain.model.Appointment

data class AppointmentState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
