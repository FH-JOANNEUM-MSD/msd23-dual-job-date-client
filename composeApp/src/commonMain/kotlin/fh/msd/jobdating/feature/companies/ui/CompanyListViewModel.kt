

// --- feature/companies/ui/CompanyListViewModel.kt (updated with navigation) ---
package fh.msd.jobdating.feature.companies.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class CompanyNavigation {
    data object ToAppointments : CompanyNavigation()
}

class CompanyListViewModel(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompanyListState())
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<CompanyNavigation>()
    val navigation = _navigation.asSharedFlow()

    init {
        loadCompanies()
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val companies = repository.getActiveCompanies()
                _state.update { it.copy(companies = companies, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onEvent(event: CompanyListEvent) {
        when (event) {
            is CompanyListEvent.Vote -> vote(event.companyId, event.vote)
        }
    }

    private fun vote(companyId: String, vote: VoteType) {
        viewModelScope.launch {
            try {
                repository.submitVote(companyId, vote)
                _state.update { current ->
                    val nextIndex = current.currentIndex + 1
                    current.copy(
                        currentIndex = nextIndex,
                        isDone = nextIndex >= current.companies.size
                    )
                }
                if (_state.value.isDone) {
                    _navigation.emit(CompanyNavigation.ToAppointments)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
