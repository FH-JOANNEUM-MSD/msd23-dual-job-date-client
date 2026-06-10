package fh.msd.jobdating.core.startup

/**
 * Drives the app's first screen. While [isLoading] is true the splash is shown;
 * once session restore resolves, [isLoggedIn] decides Main vs Login.
 */
data class StartupState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false
)
