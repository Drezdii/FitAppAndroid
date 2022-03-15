package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme
import com.bartoszdrozd.fitapp.utils.toWorkoutDate
import com.bartoszdrozd.fitapp.utils.toWorkoutDuration

interface WorkoutActions {
    fun updateSet(set: WorkoutSet, exerciseId: Long)
    fun addSet(exercise: Exercise)
    fun deleteSet(set: WorkoutSet, exerciseId: Long)
    fun addExercise(exerciseInfoId: Int)
    fun deleteExercise(exercise: Exercise)
    fun saveWorkout()
}

@Composable
fun WorkoutScreen(workoutViewModel: WorkoutViewModel, workoutId: Long) {
    val state by workoutViewModel.workoutUiState.collectAsState()

    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkout(workoutId)
    }

    val actions = object : WorkoutActions {
        override fun updateSet(set: WorkoutSet, exerciseId: Long) {
            workoutViewModel.updateSet(set, exerciseId)
        }

        override fun addSet(exercise: Exercise) {
            workoutViewModel.addSet(exercise)
        }

        override fun deleteSet(set: WorkoutSet, exerciseId: Long) {
            workoutViewModel.deleteSet(set, exerciseId)
        }

        override fun addExercise(exerciseInfoId: Int) {
            workoutViewModel.addExercise(exerciseInfoId)
        }

        override fun deleteExercise(exercise: Exercise) {
            workoutViewModel.deleteExercise(exercise)
        }

        override fun saveWorkout() {
            workoutViewModel.saveWorkout()
        }
    }

    state.workout?.let {
        WorkoutView(it, actions)
    }
}

@Composable
private fun WorkoutView(workout: Workout, actions: WorkoutActions) {
    val smallPadding = dimensionResource(R.dimen.small_padding)
    LazyColumn(Modifier.padding(horizontal = smallPadding)) {
        item {
            WorkoutHeader(workout, actions::saveWorkout)
        }

        item {
            NewExerciseBar(addExercise = actions::addExercise)
        }

        items(
            items = workout.exercises,
            key = { exercise ->
                exercise.id
            }
        )
        { exercise ->
            ExerciseItem(exercise, actions = actions)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewExerciseBar(addExercise: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    val smallPadding = dimensionResource(R.dimen.small_padding)
    val verticalPadding = dimensionResource(R.dimen.vertical_padding)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding, horizontal = smallPadding)
            .horizontalScroll(scrollState),
    ) {
        com.bartoszdrozd.fitapp.ui.components.Chip(onClick = { addExercise(1) }) {
            Text("Add")
        }

        Spacer(modifier = Modifier.width(smallPadding))

        com.bartoszdrozd.fitapp.ui.components.Chip(onClick = { addExercise(1) }) {
            Text("Bench")
        }

        Spacer(modifier = Modifier.width(smallPadding))

        com.bartoszdrozd.fitapp.ui.components.Chip(onClick = { addExercise(2) }) {
            Text("Deadlift")
        }

        Spacer(modifier = Modifier.width(smallPadding))

        com.bartoszdrozd.fitapp.ui.components.Chip(onClick = { addExercise(3) }) {
            Text("Squat")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutHeader(workout: Workout, saveWorkout: () -> Unit) {
    val smallPadding = dimensionResource(R.dimen.small_padding)
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = smallPadding)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .height(IntrinsicSize.Min)
        ) {

            val durationText = if (workout.endDate != null && workout.startDate != null) {
                workout.endDate.minus(workout.startDate).toWorkoutDuration()
            } else {
                stringResource(R.string.duration_placeholder)
            }

            WorkoutDateRow(date = workout.date.toWorkoutDate(), duration = durationText)
        }

        Button(onClick = { saveWorkout() }, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutPreview() {
    val workout = Workout(
        1, exercises = listOf(
            Exercise(1, 2),
            Exercise(
                2, 2, sets = listOf(
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, false),
                )
            )
        )
    )

    val actions = object : WorkoutActions {
        override fun updateSet(set: WorkoutSet, exerciseId: Long) {
            TODO("Not yet implemented")
        }

        override fun addSet(exercise: Exercise) {
            TODO("Not yet implemented")
        }

        override fun deleteSet(set: WorkoutSet, exerciseId: Long) {
            TODO("Not yet implemented")
        }

        override fun addExercise(exerciseInfoId: Int) {
            TODO("Not yet implemented")
        }

        override fun deleteExercise(exercise: Exercise) {
            TODO("Not yet implemented")
        }

        override fun saveWorkout() {
            TODO("Not yet implemented")
        }

    }

    FitAppTheme {
        WorkoutView(workout = workout, actions = actions)
    }
}