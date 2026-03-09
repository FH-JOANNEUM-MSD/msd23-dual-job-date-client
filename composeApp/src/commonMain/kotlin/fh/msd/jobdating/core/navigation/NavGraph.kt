
package fh.msd.jobdating.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fh.msd.jobdating.feature.appointments.ui.AppointmentScreen
import fh.msd.jobdating.feature.auth.ui.LoginScreen
import fh.msd.jobdating.feature.companies.ui.CompanyListScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Companies : Screen("companies")
    data object Appointments : Screen("appointments")
}

@Composable
fun NavGraph(startDestination: String = Screen.Login.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Companies.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Companies.route) {
            CompanyListScreen(
                onDone = {
                    navController.navigate(Screen.Appointments.route) {
                        popUpTo(Screen.Companies.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Appointments.route) {
            AppointmentScreen()
        }
    }
}