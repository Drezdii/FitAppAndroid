package com.bartoszdrozd.fitapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.domain.stats.GetLatestBodyWeightEntryUseCase
import com.bartoszdrozd.fitapp.domain.stats.SaveBodyWeightEntryUseCase
import com.bartoszdrozd.fitapp.ui.Screen
import com.bartoszdrozd.fitapp.ui.TopAppBarState
import com.bartoszdrozd.fitapp.ui.auth.LoginActivity
import com.bartoszdrozd.fitapp.ui.challenges.ChallengesScreen
import com.bartoszdrozd.fitapp.ui.components.AvatarWithUsername
import com.bartoszdrozd.fitapp.ui.components.BodyWeightDialog
import com.bartoszdrozd.fitapp.ui.creator.ProgramsScreen
import com.bartoszdrozd.fitapp.ui.progress.ProgressScreen
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme
import com.bartoszdrozd.fitapp.ui.workout.WorkoutListScreen
import com.bartoszdrozd.fitapp.ui.workout.WorkoutScreen
import com.bartoszdrozd.fitapp.ui.workout.planned.PlannedWorkoutsScreen
import com.bartoszdrozd.fitapp.utils.data
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var saveBwEntryUseCase: SaveBodyWeightEntryUseCase

    @Inject
    lateinit var getLatestBwEntryUseCase: GetLatestBodyWeightEntryUseCase

    @Inject
    lateinit var userRepository: IUserRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screens = listOf(Screen.Timeline, Screen.Planned, Screen.Progress, Screen.Programs)

        FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Create the NotificationChannel used for active workouts
        val name = "Active workout channel"
        val descriptionText = "Workout active channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("WORKOUT_ACTIVE", name, importance).apply {
            description = descriptionText
            setSound(null, null)
        }

        // Register the channel with the system
        val notificationManager: NotificationManager = getSystemService()!!
        notificationManager.createNotificationChannel(channel)

        setContent {
            FitAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val shouldExpandFAB = remember { mutableStateOf(true) }
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override suspend fun onPreFling(available: Velocity): Velocity {
                            shouldExpandFAB.value = when {
                                available.y == 0F -> true
                                available.y > 3000 -> true
                                available.y < 3000 -> false
                                else -> true
                            }

                            return Velocity.Zero
                        }
                    }
                }

                // Padding used to hide navigation bar when keyboard is open
                val navBarTopPadding = (WindowInsets.ime.asPaddingValues()
                    .calculateBottomPadding().value.minus(130)).coerceAtLeast(0f)

                var openBwDialog by remember { mutableStateOf(false) }

                var currentBodyWeight by remember { mutableFloatStateOf(0F) }

                var userName by remember { mutableStateOf("") }

                if (openBwDialog) {
                    BodyWeightDialog(
                        onDismiss = { openBwDialog = false },
                        onConfirm = { entry ->
                            scope.launch {
                                saveBwEntryUseCase(entry)
                                openBwDialog = false
                            }
                        },
                        startValue = currentBodyWeight
                    )
                }

                LaunchedEffect(Unit) {
                    scope.launch {
                        currentBodyWeight = getLatestBwEntryUseCase(Unit).data?.weight ?: 0f
                    }
                }

                LaunchedEffect(Unit) {
                    scope.launch {
                        userName = userRepository.getUsername()
                    }
                }

                val defaultTopAppBarState = TopAppBarState(
                    { AvatarWithUsername(userName) },
                    {
                        Row(
                            Modifier
                                .clickable { openBwDialog = true }
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.MonitorWeight,
                                contentDescription = "Current body weight",
                                Modifier.padding(end = 8.dp)
                            )
                            Text(text = currentBodyWeight.toString() + "kg")
                        }
                    }
                )

                var appBarState by remember { mutableStateOf(TopAppBarState({}, {})) }

                val setAppBarState: (TopAppBarState) -> Unit = { newState ->
                    appBarState = newState
                }

                LaunchedEffect(currentDestination?.route) {
                    if (currentDestination?.route != "workout/{workoutId}") {
                        setAppBarState(defaultTopAppBarState)
                    }
                }

                Scaffold(
                    Modifier
                        .nestedScroll(nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = appBarState.title,
                            actions = appBarState.actions,
                            navigationIcon = {
                                if (appBarState.showBackButton) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            Icons.Filled.ArrowBack,
                                            contentDescription = stringResource(R.string.navigate_back)
                                        )
                                    }
                                }
                            })
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    floatingActionButton = {
                        when (currentDestination?.route) {
                            "timeline_HIDDEN_FOR_NOW" -> {
                                ExtendedFloatingActionButton(
                                    text = { Text(text = stringResource(id = R.string.add_workout)) },
                                    icon = {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Add workout"
                                        )
                                    },
                                    onClick = { navController.navigate("workout/0") },
                                    expanded = shouldExpandFAB.value,
                                )
                            }
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            Modifier.padding(top = navBarTopPadding.dp)
                        ) {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            screen.icon,
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
                    val navHostPadding = PaddingValues(
                        innerPadding.calculateStartPadding(LayoutDirection.Ltr)
                            .plus(dimensionResource(R.dimen.small_padding)),
                        innerPadding.calculateTopPadding(),
                        innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(
                            dimensionResource(R.dimen.small_padding)
                        ),
                        innerPadding.calculateBottomPadding()
                    )

                    NavHost(
                        navController = navController,
                        startDestination = "timeline",
                        Modifier.padding(navHostPadding)
                    ) {
                        composable("timeline") {
                            WorkoutListScreen(
                                onWorkoutClick = { workoutId -> navController.navigate("workout/$workoutId") },
                                viewModel = hiltViewModel(),
                                showSnackbar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message.message)
                                    }
                                }
                            )
                        }
                        composable(
                            "workout/{workoutId}",
                            arguments = listOf(navArgument("workoutId") {
                                type = NavType.LongType
                            }),
                            deepLinks = listOf(navDeepLink {
                                uriPattern = "fitapp://workout/{workoutId}"
                            })
                        ) { backStackEntry ->
                            WorkoutScreen(
                                workoutViewModel = hiltViewModel(),
                                backStackEntry.arguments?.getLong("workoutId") ?: -1L,
                                showSnackbar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message.message)
                                    }
                                },
                                onWorkoutDeleted = {
                                    navController.navigate("timeline")
                                },
                                setTopAppBarState = setAppBarState
                            )
                        }
                        composable("programs") {
                            ProgramsScreen(
                                creatorViewModel = hiltViewModel(),
                                onProgramSaved = {
                                    navController.navigate("timeline")
                                },
                                showSnackbar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message.message)
                                    }
                                }
                            )
                        }
                        composable("planned") {
                            PlannedWorkoutsScreen(
                                viewModel = hiltViewModel(),
                                onWorkoutClick = { workoutId -> navController.navigate("workout/$workoutId") },
                                setAppBarState = setAppBarState
                            )
                        }
                        composable("progress") {
                            ProgressScreen(
                                viewModel = hiltViewModel(),
                                onViewChallenges = { navController.navigate("challenges") })
                        }
                        composable("challenges") {
                            ChallengesScreen(
                                viewModel = hiltViewModel(),
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
    }
}