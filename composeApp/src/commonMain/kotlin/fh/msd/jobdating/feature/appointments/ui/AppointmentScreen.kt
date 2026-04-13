package fh.msd.jobdating.feature.appointments.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fh.msd.jobdating.feature.companies.ui.components.CompanyDetailDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.appointments_default_event_name
import dualjobdating.composeapp.generated.resources.appointments_no_appointments
import dualjobdating.composeapp.generated.resources.appointments_your_appointments
import dualjobdating.composeapp.generated.resources.error_prefix

@Composable
fun AppointmentScreen(
    viewModel: AppointmentViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedCompanyId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> Text(stringResource(Res.string.error_prefix, state.error ?: ""))

            state.appointments.isEmpty() -> Text(
                text = stringResource(Res.string.appointments_no_appointments),
                style = MaterialTheme.typography.bodyLarge
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(1.dp)) }

                item {
                    EventCard(
                        eventName = state.eventName ?: stringResource(Res.string.appointments_default_event_name),
                        eventDate = formatAustrianDate(state.eventDate ?: ""),
                        eventLocation = state.eventLocation ?: "",
                        eventDescription = state.eventDescription ?: ""
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text(
                        text = stringResource(Res.string.appointments_your_appointments),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item { Spacer(modifier = Modifier.height(4.dp)) }

                items(state.appointments) { appointment ->
                    Card(
                        onClick = { selectedCompanyId = appointment.companyId },
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = appointment.companyName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatAppointmentTime(appointment.timeSlot),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    selectedCompanyId?.let { companyId ->
        val company = state.companies.find { it.id.toString() == companyId }
        if (company != null) {
            CompanyDetailDialog(
                company = company,
                onDismiss = { selectedCompanyId = null },
                onVote = { selectedCompanyId = null }
            )
        }
    }
}

@Composable
private fun EventCard(
    eventName: String,
    eventDate: String,
    eventLocation: String,
    eventDescription: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = eventName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = eventDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (eventLocation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = eventLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (eventDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = eventDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatAustrianDate(isoDate: String): String {
    if (isoDate.isBlank()) return ""

    val parts = isoDate.split("-")
    if (parts.size != 3) return isoDate

    return "${parts[2]}.${parts[1]}.${parts[0]}"
}

private fun formatAppointmentTime(timeSlot: String): String {
    val parts = timeSlot.split(" • ")
    if (parts.size != 2) return timeSlot

    val date = formatAustrianDate(parts[0])
    return "$date • ${parts[1]}"
}