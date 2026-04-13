package fh.msd.jobdating.feature.appointments.ui

import fh.msd.jobdating.feature.appointments.domain.model.Appointment
import fh.msd.jobdating.feature.companies.domain.model.Company

data class AppointmentState(
    val appointments: List<Appointment> = emptyList(),
    val companies: List<Company> = emptyList(),
    val eventName: String? = null,
    val eventDate: String? = null,
    val eventLocation: String? = null,
    val eventDescription: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)