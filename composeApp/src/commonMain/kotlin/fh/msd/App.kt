package fh.msd

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fh.msd.jobdating.core.di.appModule
import fh.msd.jobdating.core.navigation.NavGraph
import fh.msd.jobdating.core.navigation.Screen
import fh.msd.jobdating.core.startup.SplashScreen
import fh.msd.jobdating.core.startup.StartupViewModel
import fh.msd.jobdating.core.ui.theme.DualJobDatingTheme
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        DualJobDatingTheme {
            val startupViewModel: StartupViewModel = koinViewModel()
            val state by startupViewModel.state.collectAsStateWithLifecycle()

            if (state.isLoading) {
                // Splash while we restore the persisted session (waits for
                // Supabase to finish loading from storage).
                SplashScreen()
            } else {
                NavGraph(
                    startDestination = if (state.isLoggedIn) Screen.Main else Screen.Login
                )
            }
        }
    }
}
