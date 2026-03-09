

// --- feature/companies/ui/CompanyListScreen.kt (updated with onDone callback) ---
package fh.msd.jobdating.feature.companies.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.components.CompanyCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompanyListScreen(
    onDone: () -> Unit,
    viewModel: CompanyListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { nav ->
            when (nav) {
                is CompanyNavigation.ToAppointments -> onDone()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> Text("Error: ${state.error}")

            state.isDone -> CircularProgressIndicator()

            else -> {
                val company = state.companies[state.currentIndex]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CompanyCard(
                        company = company,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(48.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.onEvent(
                                    CompanyListEvent.Vote(company.id, VoteType.DISLIKE)
                                )
                            },
                            containerColor = Color.Red
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dislike")
                        }
                        FloatingActionButton(
                            onClick = {
                                viewModel.onEvent(
                                    CompanyListEvent.Vote(company.id, VoteType.LIKE)
                                )
                            },
                            containerColor = Color.Green
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "Like")
                        }
                    }
                }
            }
        }
    }
}