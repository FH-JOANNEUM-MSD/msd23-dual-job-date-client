package fh.msd

import androidx.compose.runtime.Composable
import fh.msd.jobdating.core.navigation.NavGraph
import org.koin.compose.KoinApplication
import fh.msd.jobdating.core.di.appModule
import fh.msd.jobdating.core.ui.theme.DualJobDatingTheme

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        DualJobDatingTheme {
            NavGraph()
        }
    }
}
