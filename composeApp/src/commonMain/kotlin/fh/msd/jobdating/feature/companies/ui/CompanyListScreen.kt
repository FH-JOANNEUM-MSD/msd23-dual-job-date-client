package fh.msd.jobdating.feature.companies.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fh.msd.jobdating.core.ui.theme.DislikeRed
import fh.msd.jobdating.core.ui.theme.LikeGreen
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.components.CompanyDetailDialog
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CompanyListScreen(
    viewModel: CompanyListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedCompany by remember { mutableStateOf<Company?>(null) }

    val notVotedCompanies = state.companies.filter { it.vote == null }
    val votedCompanies = state.companies.filter { it.vote != null }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 16.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(1.dp)) }

                if (notVotedCompanies.isNotEmpty()) {
                    item {
                        Text(
                            text = "Not Voted",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(notVotedCompanies) { company ->
                        CompanyCard(
                            company = company,
                            onClick = { selectedCompany = company }
                        )
                    }
                }

                if (votedCompanies.isNotEmpty()) {
                    item {
                        Text(
                            text = "Voted",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }

                    items(votedCompanies) { company ->
                        CompanyCard(
                            company = company,
                            onClick = { selectedCompany = company }
                        )
                    }
                }
            }
        }
    }

    selectedCompany?.let { company ->
        CompanyDetailDialog(
            company = company,
            onDismiss = { selectedCompany = null },
            onVote = { vote ->
                viewModel.onEvent(CompanyListEvent.Vote(company.id, vote))
                selectedCompany = null
            }
        )
    }
}

@Composable
private fun CompanyCard(
    company: Company,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = company.name.take(30) + if (company.name.length > 30) "..." else "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = company.description.take(30) + if (company.description.length > 30) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (company.vote) {
                            VoteType.LIKE -> Icons.Outlined.CheckCircle
                            VoteType.DISLIKE -> Icons.Outlined.Cancel
                            VoteType.NEUTRAL -> Icons.Outlined.RemoveCircleOutline
                            null -> Icons.Outlined.HelpOutline
                        },
                        contentDescription = null,
                        tint = when (company.vote) {
                            VoteType.LIKE -> LikeGreen
                            VoteType.DISLIKE -> DislikeRed
                            VoteType.NEUTRAL -> NeutralOrange
                            null -> Color.Gray
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}