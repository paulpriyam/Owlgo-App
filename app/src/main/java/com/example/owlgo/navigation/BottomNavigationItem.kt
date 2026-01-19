package com.example.owlgo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItem(val route: String, val title: String,val icon: ImageVector) {
    object DashboardScreen: BottomNavigationItem("dashboard", "Dashboard", Icons.Filled.Home)
    object AllProblemsScreen: BottomNavigationItem("all_problems", "All Problems", Icons.Filled.Star)
    object TodayScreen: BottomNavigationItem("today", "Today", Icons.Filled.DateRange)
    object SettingsScreen: BottomNavigationItem("settings", "Settings", Icons.Filled.Settings)
}