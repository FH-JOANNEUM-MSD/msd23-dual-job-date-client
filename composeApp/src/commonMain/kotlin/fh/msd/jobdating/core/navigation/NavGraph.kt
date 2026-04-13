package fh.msd.jobdating.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import fh.msd.jobdating.core.ui.AppBackground
import fh.msd.jobdating.feature.appointments.ui.AppointmentScreen
import fh.msd.jobdating.feature.auth.ui.LoginScreen
import fh.msd.jobdating.feature.companies.ui.CompanyListScreen
import fh.msd.jobdating.feature.companies.ui.CompanySwipeScreen
import fh.msd.jobdating.feature.profile.ui.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Login : Screen
    @Serializable
    data object Main : Screen
    @Serializable
    data object CompanySwipe : Screen
    @Serializable
    data object CompanyList : Screen
    @Serializable
    data object Appointments : Screen
    @Serializable
    data object Profile : Screen
}


@Composable
fun NavGraph(startDestination: Screen = Screen.Login) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }

    ) {

        composable<Screen.Login> {
            Scaffold {
                AppBackground {
                    LoginScreen(
                        onLoginSuccess = {
                            rootNavController.navigate(Screen.Main) {
                                popUpTo(Screen.Login) { inclusive = true }
                            }
                        }
                    )

                }
            }
        }

        navigation<Screen.Main>(startDestination = Screen.CompanySwipe) {
            composable<Screen.CompanySwipe> {
                MainScreenWithBottomNav(
                    currentScreen = Screen.CompanySwipe,
                    rootNavController = rootNavController
                ) {
                    CompanySwipeScreen(
                        onNavigateToAppointments = {
                            rootNavController.navigate(Screen.Appointments)
                        }
                    )
                }
            }

            composable<Screen.CompanyList> {
                MainScreenWithBottomNav(
                    currentScreen = Screen.CompanyList,
                    rootNavController = rootNavController
                ) {
                    CompanyListScreen()
                }
            }

            composable<Screen.Appointments> {
                MainScreenWithBottomNav(
                    currentScreen = Screen.Appointments,
                    rootNavController = rootNavController
                ) {
                    AppointmentScreen()
                }
            }

            composable<Screen.Profile> {
                MainScreenWithBottomNav(
                    currentScreen = Screen.Profile,
                    rootNavController = rootNavController
                ) {
                    ProfileScreen(
                        onLogout = {
                            rootNavController.navigate(Screen.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreenWithBottomNav(
    currentScreen: Screen,
    rootNavController: androidx.navigation.NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(rootNavController) }
    ) { innerPadding ->
        AppBackground {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                content()
            }
        }
    }
}