package com.bartoszdrozd.fitapp.ui.workout

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.ui.components.Chip
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
    fun addExercise(exerciseInfoId: Int)
    fun deleteExercise(exercise: Exercise)
    fun saveWorkout()
    fun onClickExpand(exerciseId: Long)
    fun onChangeWorkoutState()
    fun updateDate(newDate: LocalDate)
    fun cancelChanges()
}

@Composable
fun WorkoutScreen(workoutViewModel: WorkoutViewModel, workoutId: Long) {
    val state by workoutViewModel.workoutUiState.collectAsState()
    val openExercises by workoutViewModel.openExercises.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        workoutViewModel.loadWorkout(workoutId)

        workoutViewModel.savingResultEvent.collect {
            if (it is Result.Success) {
                Toast.makeText(context, context.getText(R.string.saved), Toast.LENGTH_SHORT)
                    .show()
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

        override fun addExercise(exerciseInfoId: Int) {
            workoutViewModel.addExercise(exerciseInfoId)
        }

        override fun deleteExercise(exercise: Exercise) {
            workoutViewModel.deleteExercise(exercise)
        }

        override fun saveWorkout() {
            workoutViewModel.saveWorkout()
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
            actions::saveWorkout,
            actions::onChangeWorkoutState,
            actions::updateDate,
            actions::cancelChanges,
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
        Chip(onClick = { addExercise(1) }) {
            Text(stringResource(R.string.deadlift))
        }

        Spacer(modifier = Modifier.width(smallPadding))

        Chip(onClick = { addExercise(2) }) {
            Text(stringResource(R.string.bench))
        }

        Spacer(modifier = Modifier.width(smallPadding))

        Chip(onClick = { addExercise(3) }) {
            Text(stringResource(R.string.squat))
        }

        Spacer(modifier = Modifier.width(smallPadding))

        Chip(onClick = { addExercise(4) }) {
            Text(stringResource(R.string.ohp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutHeader(
    workout: Workout,
    saveWorkout: () -> Unit,
    changeWorkoutState: () -> Unit,
    updateDate: (LocalDate) -> Unit,
    cancelChanges: () -> Unit,
    isDirty: Boolean
) {
    var durationText by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.small_padding))
    ) {
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
                            Clock.System.now().minus(workout.startDate).toWorkoutDuration()
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
                    updateDate(newDate)
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
                WorkoutStateButton(changeWorkoutState, isStarted = workout.startDate != null)
            }
        }

        // Show Save/Cancel buttons
        if (isDirty) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (workout.id != 0L) {
                    Button(onClick = { cancelChanges() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = { saveWorkout() }) {
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

        override fun addExercise(exerciseInfoId: Int) {
            TODO("Not yet implemented")
        }

        override fun deleteExercise(exercise: Exercise) {
            TODO("Not yet implemented")
        }

        override fun saveWorkout() {
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