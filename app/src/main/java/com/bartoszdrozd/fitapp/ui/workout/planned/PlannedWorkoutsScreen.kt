package com.bartoszdrozd.fitapp.ui.workout.planned

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.ui.TopAppBarState
import com.bartoszdrozd.fitapp.ui.workout.WorkoutList

@Composable
fun PlannedWorkoutsScreen(
    viewModel: PlannedWorkoutsViewModel,
    onWorkoutClick: (Long) -> Unit,
    setAppBarState: (TopAppBarState) -> Unit
) {
    val workouts by viewModel.workouts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPlannedWorkouts()
    }

    LaunchedEffect(workouts.size) {
        val newAppBarState = TopAppBarState(
            { Text(stringResource(R.string.num_of_planned_workouts, workouts.size)) },
            {},
            showBackButton = true
        )

        setAppBarState(newAppBarState)
    }

    WorkoutList(workouts, emptyList(), onWorkoutClick)
}