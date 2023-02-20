package com.bartoszdrozd.fitapp.ui.workout

import android.app.*
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.SnackbarMessage
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme
import com.bartoszdrozd.fitapp.utils.EventType
import com.bartoszdrozd.fitapp.utils.programDetailsToNameId
import com.bartoszdrozd.fitapp.utils.toWorkoutDate
import com.bartoszdrozd.fitapp.utils.toWorkoutDuration
import kotlinx.coroutines.delay
import kotlinx.datetime.*

interface IWorkoutActions {
    fun updateSet(set: WorkoutSet, exerciseId: Long)
    fun addSet(exercise: Exercise)
    fun deleteSet(set: WorkoutSet, exerciseId: Long)
    fun addExercise(exerciseType: ExerciseType)
    fun deleteExercise(exercise: Exercise)
    fun saveWorkout()
    fun deleteWorkout()
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
    val isActive = state.workout.startDate != null && state.workout.endDate == null

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Show notification
            }
        }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkout(workoutId)

        workoutViewModel.events.collect {
            when (it) {
                EventType.Deleted -> {
                    showSnackbar(SnackbarMessage(context.getString(R.string.workout_deleted)))
                    NotificationManagerCompat.from(context).cancel(state.workout.id.toInt())
                    onWorkoutDeleted()
                }
                is EventType.Error -> showSnackbar(SnackbarMessage(context.getString(R.string.general_error)))
                EventType.Loading -> TODO()
                EventType.Saved -> showSnackbar(SnackbarMessage(context.getString(R.string.saved)))
                else -> {}
            }
        }
    }

    val programName =
        state.workout.workoutProgramDetails?.let { stringResource(programDetailsToNameId(it)) }

    val intent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(
            Intent(
                Intent.ACTION_VIEW,
                "fitapp://workout/${state.workout.id}".toUri()
            )
        )
        getPendingIntent(workoutId.toInt(), PendingIntent.FLAG_IMMUTABLE)
    }

    val builder = Notification.Builder(context, "WORKOUT_ACTIVE")
        .setSmallIcon(R.drawable.ic_deadlift)
        .setContentTitle("Active workout ${programName ?: ""}")
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .setContentIntent(intent)

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

        override fun onChangeWorkoutState() {
            workoutViewModel.changeCompletionState()
        }

        override fun updateDate(newDate: LocalDate) {
            workoutViewModel.updateDate(newDate)
        }

        override fun cancelChanges() {
            workoutViewModel.cancelChanges()
        }
    }

    WorkoutView(state.workout, actions, state.isDirty)
}

@Composable
private fun WorkoutView(
    workout: Workout,
    actions: IWorkoutActions,
    isDirty: Boolean
) {
    val scrollState = rememberScrollState()

    // Temporary fix for lost focus in TextFields when using LazyColumn
    Column(Modifier.verticalScroll(scrollState)) {
        WorkoutHeader(workout, actions, isDirty)

        NewExerciseBar(actions::addExercise)

        for (exercise in workout.exercises) {
            ExerciseItem(
                exercise,
                actions = actions,
            )
        }
    }

//    LazyColumn {
//        item {
//            WorkoutHeader(workout, actions, isDirty)
//        }
//
//        item {
//            NewExerciseBar(actions::addExercise)
//        }
//
//        items(
//            items = workout.exercises,
//            key = { exercise ->
//                exercise.id
//            }
//        )
//        { exercise ->
//            ExerciseItem(
//                exercise,
//                actions = actions,
//            )
//        }
//    }
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
    var openDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(Clock.System.now().toEpochMilliseconds())

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.small_padding))
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.small_padding))) {
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

                if (openDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { openDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = { openDatePicker = false }) {
                                Text(stringResource(R.string.save))
                            }
                            if (datePickerState.selectedDateMillis != null) {
                                actions.updateDate(
                                    Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                        .toLocalDateTime(
                                            TimeZone.UTC
                                        ).date
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openDatePicker = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }) {
                        DatePicker(state = datePickerState)
                    }
                }

                WorkoutDateRow(
                    date = workout.date?.toWorkoutDate(),
                    duration = durationText,
                    onDateClick = { openDatePicker = true },
                    isEditing = true
                )
            }

            // Show Start/Finish button
            if (workout.startDate == null || workout.endDate == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
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
        override fun updateSet(set: WorkoutSet, exerciseId: Long) {}

        override fun addSet(exercise: Exercise) {}

        override fun deleteSet(set: WorkoutSet, exerciseId: Long) {}

        override fun addExercise(exerciseType: ExerciseType) {}

        override fun deleteExercise(exercise: Exercise) {}

        override fun saveWorkout() {}

        override fun deleteWorkout() {}

        override fun onChangeWorkoutState() {}

        override fun updateDate(newDate: LocalDate) {}

        override fun cancelChanges() {}
    }

    FitAppTheme {
        WorkoutView(workout = workout, actions = actions, true)
    }
}