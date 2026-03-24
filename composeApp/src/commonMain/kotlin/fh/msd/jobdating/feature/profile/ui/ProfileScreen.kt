package fh.msd.jobdating.feature.profile.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    var personalExpanded by remember { mutableStateOf(true) }
    var passwordExpanded by remember { mutableStateOf(false) }
    var legalExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { nav ->
            when (nav) {
                is ProfileNavigation.ToLogin -> onLogout()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column {
                    SectionHeader(
                        title = "Personal Information",
                        expanded = personalExpanded,
                        onToggle = { personalExpanded = !personalExpanded }
                    )
                    AnimatedVisibility(visible = personalExpanded) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Name: ${state.name}")
                            Text(text = "Email: ${state.email}")
                        }
                    }

                    Divider()

                    SectionHeader(
                        title = "Change Password",
                        expanded = passwordExpanded,
                        onToggle = { passwordExpanded = !passwordExpanded }
                    )
                    AnimatedVisibility(visible = passwordExpanded) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = state.currentPassword,
                                onValueChange = { viewModel.onEvent(ProfileEvent.CurrentPasswordChanged(it)) },
                                label = { Text("Current Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = state.newPassword,
                                onValueChange = { viewModel.onEvent(ProfileEvent.NewPasswordChanged(it)) },
                                label = { Text("New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = state.confirmPassword,
                                onValueChange = { viewModel.onEvent(ProfileEvent.ConfirmPasswordChanged(it)) },
                                label = { Text("Confirm Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            state.passwordError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            state.passwordSuccess?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(
                                onClick = { viewModel.onEvent(ProfileEvent.ChangePassword) },
                                enabled = !state.isChangingPassword,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) {
                                if (state.isChangingPassword) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Change Password", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }

                    Divider()

                    SectionHeader(
                        title = "Legal",
                        expanded = legalExpanded,
                        onToggle = { legalExpanded = !legalExpanded }
                    )
                    AnimatedVisibility(visible = legalExpanded) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                        ) {
                            TextButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Impressum")
                            }
                            TextButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Data Privacy")
                            }
                        }
                    }

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onEvent(ProfileEvent.Logout) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null
        )
    }
}