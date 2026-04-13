package fh.msd.jobdating.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.app_name
import dualjobdating.composeapp.generated.resources.login_button
import dualjobdating.composeapp.generated.resources.login_email
import dualjobdating.composeapp.generated.resources.login_password
import dualjobdating.composeapp.generated.resources.logo
import dualjobdating.composeapp.generated.resources.logo_dark
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { nav ->
            when (nav) {
                is LoginNavigation.ToCompanies -> onLoginSuccess()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSystemInDarkTheme()) {
                    Image(
                        painter = painterResource(Res.drawable.logo_dark),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .drawBehind {
                                val gradient = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF444444),
                                        Color(0xFF222222)
                                    ),
                                    center = center,
                                    radius = size.width / 2
                                )
                                drawCircle(brush = gradient)
                            }
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2A2A))
                    )
                } else {
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                    label = { Text(stringResource(Res.string.login_email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                    label = { Text(stringResource(Res.string.login_password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                state.error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.onEvent(LoginEvent.Submit) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text(stringResource(Res.string.login_button), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}