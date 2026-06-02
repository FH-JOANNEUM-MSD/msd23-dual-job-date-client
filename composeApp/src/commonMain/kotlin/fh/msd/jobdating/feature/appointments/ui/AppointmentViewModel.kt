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
            println("[AppointmentVM] loadAppointments started")
            _state.update { it.copy(isLoading = true, hasError = false) }
            try {
                val appointments = repository.getMyAppointments()
                println("[AppointmentVM] appointments loaded: count=${appointments.size}")
                val eventInfo = repository.getEventInfo()
                println("[AppointmentVM] eventInfo loaded: name=${eventInfo.name}, date=${eventInfo.date}")
                val companies = companyRepository.getActiveCompanies()
                println("[AppointmentVM] companies loaded: count=${companies.size}")

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
                println("[AppointmentVM] state updated successfully")
            } catch (e: Exception) {
                println("[AppointmentVM] ERROR: ${e::class.simpleName}: ${e.message}")
                _state.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }
}
