
// --- App.kt ---
package fh.msd

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import fh.msd.jobdating.core.navigation.NavGraph
import org.koin.compose.KoinApplication
import fh.msd.jobdating.core.di.appModule

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        MaterialTheme {
            NavGraph()
        }
    }
}
