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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.profile_change_password_button
import dualjobdating.composeapp.generated.resources.profile_confirm_password
import dualjobdating.composeapp.generated.resources.profile_current_password
import dualjobdating.composeapp.generated.resources.profile_data_privacy
import dualjobdating.composeapp.generated.resources.profile_email
import dualjobdating.composeapp.generated.resources.profile_impressum
import dualjobdating.composeapp.generated.resources.profile_logout
import dualjobdating.composeapp.generated.resources.profile_new_password
import dualjobdating.composeapp.generated.resources.profile_role
import dualjobdating.composeapp.generated.resources.profile_section_legal
import dualjobdating.composeapp.generated.resources.profile_section_password
import dualjobdating.composeapp.generated.resources.profile_section_personal
import dualjobdating.composeapp.generated.resources.profile_student_id

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
            .statusBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(modifier = Modifier.height(1.dp))

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
                        title = stringResource(Res.string.profile_section_personal),
                        expanded = personalExpanded,
                        onToggle = { personalExpanded = !personalExpanded }
                    )
                    AnimatedVisibility(visible = personalExpanded) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProfileInfoRow(label = stringResource(Res.string.profile_email), value = state.email)
                            ProfileInfoRow(label = stringResource(Res.string.profile_student_id), value = state.studentId)
                            ProfileInfoRow(label = stringResource(Res.string.profile_role), value = state.role)
                        }
                    }

                    Divider()

                    SectionHeader(
                        title = stringResource(Res.string.profile_section_password),
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
                                label = { Text(stringResource(Res.string.profile_current_password)) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = state.newPassword,
                                onValueChange = { viewModel.onEvent(ProfileEvent.NewPasswordChanged(it)) },
                                label = { Text(stringResource(Res.string.profile_new_password)) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = state.confirmPassword,
                                onValueChange = { viewModel.onEvent(ProfileEvent.ConfirmPasswordChanged(it)) },
                                label = { Text(stringResource(Res.string.profile_confirm_password)) },
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
                                    Text(stringResource(Res.string.profile_change_password_button), style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }

                    Divider()

                    SectionHeader(
                        title = stringResource(Res.string.profile_section_legal),
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
                                Text(stringResource(Res.string.profile_impressum))
                            }
                            TextButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(Res.string.profile_data_privacy))
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
                            text = stringResource(Res.string.profile_logout),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = stringResource(Res.string.profile_logout),
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

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}