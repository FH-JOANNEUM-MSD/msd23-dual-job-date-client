package fh.msd.jobdating.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
) {
    data object CompanySwipe : BottomNavItem(Screen.CompanySwipe, "Swipe", Icons.Default.SwapHoriz)
    data object CompanyList : BottomNavItem(Screen.CompanyList, "Companies",
        Icons.AutoMirrored.Filled.List
    )
    data object Appointments : BottomNavItem(Screen.Appointments, "Appointments", Icons.Default.DateRange)
    data object Profile : BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem.CompanySwipe,
        BottomNavItem.CompanyList,
        BottomNavItem.Appointments,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen::class.qualifiedName } == true
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    if (isSelected)
                        return@NavigationBarItem
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