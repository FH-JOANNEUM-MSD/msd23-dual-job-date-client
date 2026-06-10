package fh.msd.jobdating.core.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fh.msd.jobdating.feature.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StartupViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StartupState())
    val state = _state.asStateFlow()

    init {
        resolveSession()
    }

    private fun resolveSession() {
        viewModelScope.launch {
            // Restore can suspend (it waits for Supabase's load-from-storage).
            // Any failure falls back to login rather than hanging the splash.
            val loggedIn = try {
                repository.restoreSession()
            } catch (e: Exception) {
                false
            }
            _state.update { it.copy(isLoading = false, isLoggedIn = loggedIn) }
        }
    }
}
