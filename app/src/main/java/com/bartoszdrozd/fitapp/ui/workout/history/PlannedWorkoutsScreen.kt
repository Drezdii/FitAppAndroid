package com.bartoszdrozd.fitapp.ui.workout.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bartoszdrozd.fitapp.ui.workout.WorkoutList

@Composable
fun PlannedWorkoutsScreen(viewModel: PlannedWorkoutsViewModel, onWorkoutClick: (Long) -> Unit) {
    val workouts by viewModel.workouts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPlannedWorkouts()
    }

    WorkoutList(workouts, emptyList(), onWorkoutClick)
}