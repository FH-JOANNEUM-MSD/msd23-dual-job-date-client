package fh.msd.jobdating.feature.companies.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanyListViewModel(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompanyListState())
    val state = _state.asStateFlow()

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

    private fun vote(companyId: Int, vote: VoteType) {
        viewModelScope.launch {
            try {
                repository.submitVote(companyId, vote)
                loadCompanies()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}