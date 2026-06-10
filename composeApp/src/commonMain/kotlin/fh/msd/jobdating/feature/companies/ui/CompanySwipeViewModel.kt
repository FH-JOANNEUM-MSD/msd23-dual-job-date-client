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
            _state.update { it.copy(isLoading = true, hasError = false) }
            try {
                val allCompanies = repository.getActiveCompanies()
                val unvotedCompanies = allCompanies.filter { it.vote == null }
                _state.update {
                    it.copy(
                        companies = unvotedCompanies,
                        currentIndex = 0,
                        isLoading = false,
                        isDone = unvotedCompanies.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun onEvent(event: CompanyListEvent) {
        when (event) {
            is CompanyListEvent.Vote -> vote(event.companyId, event.vote)
        }
    }

    private fun vote(companyId: Int, vote: VoteType) {
        // Optimistically advance through the already-loaded list for snappy UX —
        // no per-swipe refetch. When the last card is voted, switch to the done
        // state instead of moving the index past the list (SwipeContent reads
        // companies[currentIndex] directly, so the index must stay in bounds).
        _state.update {
            val nextIndex = it.currentIndex + 1
            if (nextIndex >= it.companies.size) {
                it.copy(isDone = true)
            } else {
                it.copy(currentIndex = nextIndex)
            }
        }

        // Submit the vote in the background, fire-and-forget. A failed submission
        // must not wipe the user's place with a full-screen error, so the swipe
        // stands and we only swallow/log the failure here.
        viewModelScope.launch {
            try {
                repository.submitVote(companyId, vote)
            } catch (e: Exception) {
                // Non-blocking: keep the user's place; nothing to surface here.
            }
        }
    }
}
