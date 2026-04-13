package fh.msd.jobdating.feature.appointments.data.service


import fh.msd.jobdating.feature.appointments.domain.dto.AppointmentDto
import fh.msd.jobdating.feature.appointments.domain.dto.EventDto

interface AppointmentService {
    suspend fun getMyAppointments(): List<AppointmentDto>
    suspend fun getActiveEvent(): EventDto
}