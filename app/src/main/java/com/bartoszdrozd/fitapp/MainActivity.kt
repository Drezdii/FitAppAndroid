package com.bartoszdrozd.fitapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bartoszdrozd.fitapp.ui.Screen
import com.bartoszdrozd.fitapp.ui.auth.LoginActivity
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme
import com.bartoszdrozd.fitapp.ui.workout.WorkoutListScreen
import com.bartoszdrozd.fitapp.ui.workout.WorkoutScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screens = listOf(Screen.Timeline)

        FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        setContent {
            FitAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        SmallTopAppBar(title = { Text(text = "Top App Bar") })
                    },
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.Dashboard,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(screen.resourceId)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
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
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "timeline",
                        Modifier.padding(innerPadding)
                    ) {
                        composable("timeline") {
                            WorkoutListScreen(
                                onWorkoutClick = { workoutId -> navController.navigate("workout/$workoutId") },
                                viewModel = hiltViewModel()
                            )
                        }
                        composable(
                            "workout/{workoutId}",
                            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            WorkoutScreen(
                                workoutViewModel = hiltViewModel(),
                                backStackEntry.arguments?.getLong("workoutId") ?: -1L
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FitAppTheme {
        WorkoutListScreen(onWorkoutClick = {}, viewModel = viewModel())
    }
}