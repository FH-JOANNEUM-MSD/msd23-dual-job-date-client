package fh.msd.jobdating.feature.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepository
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val repository: AppointmentRepository,
    private val companyRepository: CompanyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentState())
    val state = _state.asStateFlow()

    init {
        loadAppointments()
    }

    fun onEvent(event: AppointmentEvent) {
        when (event) {
            is AppointmentEvent.Load -> loadAppointments()
        }
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val appointments = repository.getMyAppointments()
                val eventInfo = repository.getEventInfo()
                val companies = companyRepository.getActiveCompanies()

                _state.update {
                    it.copy(
                        appointments = appointments,
                        companies = companies,
                        eventName = eventInfo.name,
                        eventDate = eventInfo.date,
                        eventLocation = eventInfo.location,
                        eventDescription = eventInfo.description,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}