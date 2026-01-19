package com.example.owlgo.navigation
import androidx.navigation.compose.currentBackStackEntryAsState
import android.graphics.drawable.Icon
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(
    navController: NavController
){
    val items = listOf(
        BottomNavigationItem.DashboardScreen,
        BottomNavigationItem.AllProblemsScreen,
        BottomNavigationItem.TodayScreen,
        BottomNavigationItem.SettingsScreen
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = item.route == currentRoute,
                onClick = {
                    navController.navigate(item.route){
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }

    }
}