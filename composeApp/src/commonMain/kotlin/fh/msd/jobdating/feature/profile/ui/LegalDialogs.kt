package fh.msd.jobdating.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.company_detail_close
import dualjobdating.composeapp.generated.resources.profile_data_privacy
import dualjobdating.composeapp.generated.resources.profile_open_source
import org.jetbrains.compose.resources.stringResource

@Composable
fun DataPrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.profile_data_privacy)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrivacySection(
                    title = "What we collect",
                    body = "• Email address - used for authentication\n• Student ID - to identify your account\n• Company preferences (Like, Neutral, Dislike votes)"
                )
                PrivacySection(
                    title = "How we use it",
                    body = "Your preferences are used exclusively to facilitate matches between students and companies at the Dual Job Dating event. No data is shared with third parties."
                )
                PrivacySection(
                    title = "Data storage",
                    body = "All data is stored securely via Supabase (supabase.com), an EU-compliant cloud platform."
                )
                PrivacySection(
                    title = "Deleting your data",
                    body = "To request deletion of your personal data, contact the event organizer. Your account and all associated preferences will be permanently deleted."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.company_detail_close))
            }
        }
    )
}

@Composable
private fun PrivacySection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class LibraryInfo(val name: String, val description: String, val license: String)

private val libraries = listOf(
    LibraryInfo("Kotlin", "Programming language", "Apache 2.0"),
    LibraryInfo("Compose Multiplatform", "Declarative UI framework for Android & iOS", "Apache 2.0"),
    LibraryInfo("Ktor", "HTTP client (OkHttp / Darwin)", "Apache 2.0"),
    LibraryInfo("Coil", "Async image loading", "Apache 2.0"),
    LibraryInfo("Koin", "Dependency injection", "Apache 2.0"),
    LibraryInfo("Supabase-kt", "Auth, database & storage client", "MIT"),
    LibraryInfo("Multiplatform Settings", "Key-value storage", "Apache 2.0"),
    LibraryInfo("Navigation Compose", "In-app navigation", "Apache 2.0"),
    LibraryInfo("AndroidX Lifecycle", "Lifecycle & ViewModel support", "Apache 2.0"),
    LibraryInfo("AndroidX Activity Compose", "Compose activity integration", "Apache 2.0"),
    LibraryInfo("AndroidX Core SplashScreen", "Splash screen API", "Apache 2.0"),
    LibraryInfo("BuildKonfig", "Build-time configuration fields", "Apache 2.0"),
)

@Composable
fun OpenSourceLibrariesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.profile_open_source)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                libraries.forEach { lib ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = lib.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lib.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = lib.license,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.company_detail_close))
            }
        }
    )
}
