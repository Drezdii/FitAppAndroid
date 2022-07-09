package com.bartoszdrozd.fitapp.ui.workout

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.SnackbarMessage
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme
import com.bartoszdrozd.fitapp.utils.Result
import com.bartoszdrozd.fitapp.utils.toWorkoutDate
import com.bartoszdrozd.fitapp.utils.toWorkoutDuration
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface IWorkoutActions {
    fun updateSet(set: WorkoutSet, exerciseId: Long)
    fun addSet(exercise: Exercise)
    fun deleteSet(set: WorkoutSet, exerciseId: Long)
    fun addExercise(exerciseType: ExerciseType)
    fun deleteExercise(exercise: Exercise)
    fun saveWorkout()
    fun deleteWorkout()
    fun onClickExpand(exerciseId: Long)
    fun onChangeWorkoutState()
    fun updateDate(newDate: LocalDate)
    fun cancelChanges()
}

@Composable
fun WorkoutScreen(
    workoutViewModel: WorkoutViewModel,
    workoutId: Long,
    showSnackbar: (SnackbarMessage) -> Unit,
    onWorkoutDeleted: () -> Unit
) {
    val state by workoutViewModel.workoutUiState.collectAsState()
    val openExercises by workoutViewModel.openExercises.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkout(workoutId)

        workoutViewModel.savingResultEvent.collect {
            if (it is Result.Success) {
                showSnackbar(SnackbarMessage(context.getString(R.string.saved)))
            }
        }
    }

    LaunchedEffect(Unit) {
        workoutViewModel.deleteResultEvent.collect {
            if (it is Result.Success) {
                showSnackbar(SnackbarMessage(context.getString(R.string.workout_deleted)))
                onWorkoutDeleted()
            } else {
                showSnackbar(SnackbarMessage(context.getString(R.string.general_error)))
            }
        }
    }

    val actions = object : IWorkoutActions {
        override fun updateSet(set: WorkoutSet, exerciseId: Long) {
            workoutViewModel.updateSet(set, exerciseId)
        }

        override fun addSet(exercise: Exercise) {
            workoutViewModel.addSet(exercise)
        }

        override fun deleteSet(set: WorkoutSet, exerciseId: Long) {
            workoutViewModel.deleteSet(set, exerciseId)
        }

        override fun addExercise(exerciseType: ExerciseType) {
            workoutViewModel.addExercise(exerciseType)
        }

        override fun deleteExercise(exercise: Exercise) {
            workoutViewModel.deleteExercise(exercise)
        }

        override fun saveWorkout() {
            workoutViewModel.saveWorkout()
        }

        override fun deleteWorkout() {
            workoutViewModel.deleteWorkout()
        }

        override fun onClickExpand(exerciseId: Long) {
            workoutViewModel.onClickExpand(exerciseId)
        }

        override fun onChangeWorkoutState() {
            workoutViewModel.changeWorkoutState()
        }

        override fun updateDate(newDate: LocalDate) {
            workoutViewModel.updateDate(newDate)
        }

        override fun cancelChanges() {
            workoutViewModel.cancelChanges()
        }
    }

    // Don't display the default workout state that is set before the real workout loads in
    if (state.workout.id != -1L) {
        WorkoutView(state.workout, actions, openExercises, state.isDirty)
    }
}

@Composable
private fun WorkoutView(
    workout: Workout,
    actions: IWorkoutActions,
    openExercises: List<Long>,
    isDirty: Boolean
) {
    val smallPadding = dimensionResource(R.dimen.small_padding)
    val scrollState = rememberScrollState()

    // Temporary fix for crashes caused by LazyColumn/focused TextFields
    Column(
        Modifier
            .padding(horizontal = smallPadding)
            .verticalScroll(scrollState)
    ) {
//        item {
        WorkoutHeader(
            workout,
            actions,
            isDirty
        )
//        }

//        item {
        NewExerciseBar(addExercise = actions::addExercise)
//        }

        for (ex in workout.exercises) {
            ExerciseItem(
                exercise = ex,
                actions = actions,
                isExpanded = openExercises.contains(ex.id)
            )
        }

//        items(
//            items = workout.exercises,
//            key = { exercise ->
//                exercise.id
//            }
//        )
//        { exercise ->
//            ExerciseItem(
//                exercise,
//                actionsI = actions,
//                isExpanded = openExercises.contains(exercise.id)
//            )
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewExerciseBar(addExercise: (ExerciseType) -> Unit) {
    val scrollState = rememberScrollState()
    val smallPadding = dimensionResource(R.dimen.small_padding)
    val verticalPadding = dimensionResource(R.dimen.vertical_padding)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = verticalPadding, start = smallPadding, end = smallPadding)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
        InputChip(
            onClick = { addExercise(ExerciseType.Deadlift) },
            label = { Text(text = stringResource(id = R.string.deadlift)) }, selected = false
        )

        InputChip(
            onClick = { addExercise(ExerciseType.Bench) },
            label = { Text(text = stringResource(id = R.string.bench)) }, selected = false
        )

        InputChip(
            onClick = { addExercise(ExerciseType.Squat) },
            label = { Text(text = stringResource(id = R.string.squat)) }, selected = false
        )

        InputChip(
            onClick = { addExercise(ExerciseType.Ohp) },
            label = { Text(text = stringResource(id = R.string.ohp)) }, selected = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutHeader(
    workout: Workout,
    actions: IWorkoutActions,
    isDirty: Boolean
) {
    var durationText by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.small_padding))
    ) {
        Button(onClick = actions::deleteWorkout) {
            Text(text = "Delete workout")
        }
        Row(
            modifier = Modifier
                .padding(24.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Start the timer if workout is in progress
            if (workout.startDate != null && workout.endDate == null) {
                LaunchedEffect(Unit) {
                    while (true) {
                        durationText =
                            Clock.System.now().minus(workout.startDate)
                                .toWorkoutDuration()
                        delay(1000)
                    }
                }
            } else {
                durationText = if (workout.endDate != null && workout.startDate != null) {
                    workout.endDate.minus(workout.startDate).toWorkoutDuration()
                } else {
                    stringResource(R.string.duration_placeholder)
                }
            }

            val date =
                workout.date ?: Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date

            val datePickerDialog = DatePickerDialog(
                LocalContext.current,
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    // DatePicker uses 0-11 for months
                    val newDate = LocalDate(year, month + 1, day)
                    actions.updateDate(newDate)
                },
                date.year,
                date.monthNumber - 1,
                date.dayOfMonth
            )

            WorkoutDateRow(
                date = workout.date?.toWorkoutDate(),
                duration = durationText,
                onDateClick = { datePickerDialog.show() },
                isEditing = true
            )
        }

        // Show Start/Finish button
        if (workout.startDate == null || workout.endDate == null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                WorkoutStateButton(
                    actions::onChangeWorkoutState,
                    isStarted = workout.startDate != null
                )
            }
        }

        // Show Save/Cancel buttons
        if (isDirty) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (workout.id != 0L) {
                    Button(onClick = { actions.cancelChanges() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = { actions.saveWorkout() }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutPreview() {
    val workout = Workout(
        1, exercises = listOf(
            Exercise(1, ExerciseType.Bench),
            Exercise(
                2, ExerciseType.Deadlift, sets = listOf(
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, true),
                    WorkoutSet(1, 5, 120.0, false),
                )
            )
        )
    )

    val actions = object : IWorkoutActions {
        override fun updateSet(set: WorkoutSet, exerciseId: Long) {
            TODO("Not yet implemented")
        }

        override fun addSet(exercise: Exercise) {
            TODO("Not yet implemented")
        }

        override fun deleteSet(set: WorkoutSet, exerciseId: Long) {
            TODO("Not yet implemented")
        }

        override fun addExercise(exerciseType: ExerciseType) {
            TODO("Not yet implemented")
        }

        override fun deleteExercise(exercise: Exercise) {
            TODO("Not yet implemented")
        }

        override fun saveWorkout() {
            TODO("Not yet implemented")
        }

        override fun deleteWorkout() {
            TODO("Not yet implemented")
        }

        override fun onClickExpand(exerciseId: Long) {
            TODO("Not yet implemented")
        }

        override fun onChangeWorkoutState() {
            TODO("Not yet implemented")
        }

        override fun updateDate(newDate: LocalDate) {
            TODO("Not yet implemented")
        }

        override fun cancelChanges() {
            TODO("Not yet implemented")
        }
    }

    FitAppTheme {
        WorkoutView(workout = workout, actions = actions, emptyList(), true)
    }
}