package fh.msd.jobdating.feature.appointments.data.repository

import fh.msd.jobdating.feature.appointments.domain.model.Appointment

interface AppointmentRepository {
    suspend fun getMyAppointments(): List<Appointment>
}