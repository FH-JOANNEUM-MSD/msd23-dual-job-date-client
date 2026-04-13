package fh.msd.jobdating.feature.companies.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanySwipeViewModel(
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
                val allCompanies = repository.getActiveCompanies()
                // Only show companies that haven't been voted on (vote is null)
                val unvotedCompanies = allCompanies.filter { it.vote == null }
                _state.update {
                    it.copy(
                        companies = unvotedCompanies,
                        isLoading = false,
                        isDone = unvotedCompanies.isEmpty()
                    )
                }
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
                _state.update { current ->
                    val nextIndex = current.currentIndex + 1
                    current.copy(
                        currentIndex = nextIndex,
                        isDone = nextIndex >= current.companies.size
                    )
                }
            } catch (e: Exception) {
                println("[CompanySwipeViewModel] Vote failed: ${e.message}")
            }
        }
    }

}