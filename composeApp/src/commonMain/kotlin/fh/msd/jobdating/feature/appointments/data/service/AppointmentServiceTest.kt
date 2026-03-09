package fh.msd.jobdating.feature.appointments.data.service

import fh.msd.jobdating.feature.appointments.domain.dto.AppointmentDto

class AppointmentServiceTest : AppointmentService {

    override suspend fun getMyAppointments(): List<AppointmentDto> = listOf(
        AppointmentDto(
            id = "1",
            companyId = "1",
            companyName = "TechCorp GmbH",
            timeSlot = "09:00 - 09:15"
        ),
        AppointmentDto(
            id = "2",
            companyId = "3",
            companyName = "FinanceHub",
            timeSlot = "09:30 - 09:45"
        ),
        AppointmentDto(
            id = "3",
            companyId = "2",
            companyName = "GreenEnergy AG",
            timeSlot = "10:00 - 10:15"
        )
    )
}