package fh.msd.jobdating.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.nav_appointments
import dualjobdating.composeapp.generated.resources.nav_companies
import dualjobdating.composeapp.generated.resources.nav_profile
import dualjobdating.composeapp.generated.resources.nav_swipe

sealed class BottomNavItem(
    val screen: Screen,
    val titleRes: StringResource,
    val icon: ImageVector
) {
    data object CompanySwipe : BottomNavItem(Screen.CompanySwipe, Res.string.nav_swipe, Icons.Default.SwapHoriz)
    data object CompanyList : BottomNavItem(Screen.CompanyList, Res.string.nav_companies, Icons.AutoMirrored.Filled.List)
    data object Appointments : BottomNavItem(Screen.Appointments, Res.string.nav_appointments, Icons.Default.DateRange)
    data object Profile : BottomNavItem(Screen.Profile, Res.string.nav_profile, Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.CompanySwipe,
        BottomNavItem.CompanyList,
        BottomNavItem.Appointments,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.screen::class.qualifiedName
            } == true

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                label = { Text(stringResource(item.titleRes)) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                onClick = {
                    if (isSelected) return@NavigationBarItem
                    navController.navigate(item.screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
    }
}