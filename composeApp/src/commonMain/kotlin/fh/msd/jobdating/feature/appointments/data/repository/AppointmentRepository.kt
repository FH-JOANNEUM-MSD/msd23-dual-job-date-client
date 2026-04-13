package fh.msd.jobdating.feature.appointments.data.repository

import fh.msd.jobdating.feature.appointments.domain.model.Appointment
import fh.msd.jobdating.feature.appointments.domain.model.EventInfo

interface AppointmentRepository {
    suspend fun getMyAppointments(): List<Appointment>
    suspend fun getEventInfo(): EventInfo
}