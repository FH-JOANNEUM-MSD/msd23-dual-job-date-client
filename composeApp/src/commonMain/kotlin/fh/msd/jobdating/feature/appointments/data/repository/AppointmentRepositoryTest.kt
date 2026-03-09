package fh.msd.jobdating.feature.appointments.data.repository

import fh.msd.jobdating.feature.appointments.data.service.AppointmentService
import fh.msd.jobdating.feature.appointments.domain.model.Appointment

class AppointmentRepositoryTest(
    private val service: AppointmentService
) : AppointmentRepository {

    override suspend fun getMyAppointments(): List<Appointment> =
        service.getMyAppointments().map {
            Appointment(
                id = it.id,
                companyId = it.companyId,
                companyName = it.companyName,
                timeSlot = it.timeSlot
            )
        }
}