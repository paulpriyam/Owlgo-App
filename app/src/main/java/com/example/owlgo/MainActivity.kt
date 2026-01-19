package com.example.owlgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.owlgo.navigation.BottomNavigationBar
import com.example.owlgo.navigation.BottomNavigationItem
import com.example.owlgo.screens.AllProblemsScreen
import com.example.owlgo.screens.DashboardScreen
import com.example.owlgo.screens.SettingsScreen
import com.example.owlgo.screens.TodayScreen
import com.example.owlgo.ui.theme.OwlgoTheme
import com.example.owlgo.viwmodel.ProblemsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OwlgoTheme {
                val navController = rememberNavController()
                val viewModel: ProblemsViewModel = hiltViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavigationItem.DashboardScreen.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavigationItem.DashboardScreen.route) { DashboardScreen() }
                        composable(BottomNavigationItem.AllProblemsScreen.route) { AllProblemsScreen() }
                        composable(BottomNavigationItem.TodayScreen.route) { TodayScreen() }
                        composable(BottomNavigationItem.SettingsScreen.route) { SettingsScreen() }

                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        OwlgoTheme {
            Greeting("Android")
        }
    }
}