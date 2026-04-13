package fh.msd

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false

        setContent {
            val darkTheme = isSystemInDarkTheme()

            LaunchedEffect(darkTheme) {
                val color = if (darkTheme) {
                    Color.parseColor("#1A1A1A")
                } else {
                    Color.parseColor("#FFFFFF")
                }

                window.statusBarColor = color
                window.navigationBarColor = color

                WindowCompat.getInsetsController(window, window.decorView)?.apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            App()
        }
    }
}