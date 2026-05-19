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
                println("[VM] Loaded ${allCompanies.size} companies")
                allCompanies.forEach { println("[VM] id=${it.id}, vote=${it.vote}") }

                val unvotedCompanies = allCompanies.filter { it.vote == null }
                println("[VM] ${unvotedCompanies.size} unvoted")

                _state.update {
                    it.copy(
                        companies = unvotedCompanies,
                        currentIndex = 0,
                        isLoading = false,
                        isDone = unvotedCompanies.isEmpty()
                    )
                }
            } catch (e: Exception) {
                println("[VM] Error: ${e.message}")
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
                println("[VM] VOTING: companyId=$companyId, vote=$vote")
                repository.submitVote(companyId, vote)
                println("[VM] VOTE SUCCESS, waiting for backend...")
                kotlinx.coroutines.delay(500)
                loadCompanies()
            } catch (e: Exception) {
                println("[VM] VOTE FAILED: ${e.message}")
            }
        }
    }
}