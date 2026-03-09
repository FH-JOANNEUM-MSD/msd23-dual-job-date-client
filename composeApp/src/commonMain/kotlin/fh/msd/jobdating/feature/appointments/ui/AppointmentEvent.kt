package fh.msd.jobdating.feature.appointments.ui

sealed class AppointmentEvent {
    data object Load : AppointmentEvent()
}
