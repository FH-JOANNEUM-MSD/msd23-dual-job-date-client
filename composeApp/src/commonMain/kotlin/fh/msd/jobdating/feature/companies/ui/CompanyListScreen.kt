package fh.msd.jobdating.feature.companies.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fh.msd.jobdating.core.ui.theme.DislikeRed
import fh.msd.jobdating.core.ui.theme.LikeGreen
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompanyListScreen(
    viewModel: CompanyListViewModel = koinViewModel()
) {   val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> Text("Error: ${state.error}")

            state.companies.isEmpty() -> Text(
                text = "No companies available",
                style = MaterialTheme.typography.bodyLarge
            )

            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.companies) { company ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = company.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = company.industry,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            CompanyListEvent.Vote(company.id, VoteType.DISLIKE)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbDown,
                                        contentDescription = "Dislike",
                                        tint = if (company.vote == VoteType.DISLIKE) DislikeRed else Color.Gray
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            CompanyListEvent.Vote(company.id, VoteType.LIKE)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Like",
                                        tint = if (company.vote == VoteType.LIKE) LikeGreen else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}