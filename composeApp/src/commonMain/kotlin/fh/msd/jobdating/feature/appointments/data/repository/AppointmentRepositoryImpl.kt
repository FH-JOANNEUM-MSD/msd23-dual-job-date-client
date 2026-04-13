package fh.msd.jobdating.feature.appointments.data.repository

import fh.msd.jobdating.feature.appointments.data.service.AppointmentService
import fh.msd.jobdating.feature.appointments.domain.model.Appointment
import fh.msd.jobdating.feature.appointments.domain.model.EventInfo

class AppointmentRepositoryImpl(
    private val service: AppointmentService
) : AppointmentRepository {

    override suspend fun getMyAppointments(): List<Appointment> {
        return service.getMyAppointments().map { dto ->
            Appointment(
                id = dto.id,
                companyId = dto.companyId,
                companyName = dto.companyName,
                timeSlot = dto.timeSlot
            )
        }
    }

    override suspend fun getEventInfo(): EventInfo {
        val eventDto = service.getActiveEvent()
        return EventInfo(
            name = eventDto.name,
            date = eventDto.eventDate,
            location = eventDto.location,
            description = eventDto.description
        )
    }
}