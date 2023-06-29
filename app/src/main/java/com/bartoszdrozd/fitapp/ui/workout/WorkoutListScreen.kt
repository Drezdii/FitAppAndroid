package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.SnackbarMessage
import com.bartoszdrozd.fitapp.model.workout.ExerciseType.*
import com.bartoszdrozd.fitapp.model.workout.ProgramDetails
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.utils.*

@Composable
fun WorkoutListScreen(
    viewModel: WorkoutListViewModel,
    onWorkoutClick: (Long) -> Unit,
    showSnackbar: (SnackbarMessage) -> Unit,
) {
    val workouts by viewModel.workouts.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getWorkouts()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is EventType.Error -> showSnackbar(SnackbarMessage(context.getString(R.string.error_loading_workouts)))
                else -> {}
            }
        }
    }

    val activeWorkouts = workouts.filter { it.startDate != null && it.endDate == null }
    val finishedWorkouts = workouts.filter { it.startDate != null && it.endDate != null }

    WorkoutList(finishedWorkouts, activeWorkouts, onWorkoutClick)
}

@Composable
fun WorkoutList(
    workoutList: List<Workout>,
    activeWorkoutsList: List<Workout>,
    onWorkoutClick: (Long) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        if (activeWorkoutsList.isNotEmpty()) {
            items(activeWorkoutsList) { workout ->
                WorkoutItem(workout, onWorkoutClick)
            }

            item {
                Divider(Modifier.padding(vertical = 6.dp), thickness = 1.dp)
            }
        }

        if (workoutList.isEmpty()) {
            item {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.no_workouts))
                }
            }
        }
        items(workoutList) { workout ->
            WorkoutItem(workout, onWorkoutClick)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutItem(
    workout: Workout,
    onWorkoutClick: (Long) -> Unit
) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.workout_item_vertical_padding))
            .clickable {
                onWorkoutClick(workout.id)
            }
    ) {
        Row(
            Modifier
                .padding(24.dp)
                .height(IntrinsicSize.Min)
        ) {
            val workoutIconId = exerciseTypeToIconId(workout.type)

            Icon(
                painter = painterResource(workoutIconId),
                contentDescription = null,
                Modifier
                    .padding(end = 20.dp)
                    .fillMaxHeight(0.5f)
                    .aspectRatio(1f)
                    .align(Alignment.CenterVertically)
            )

            val durationText = when {
                workout.startDate != null && workout.endDate != null -> {
                    workout.endDate.minus(workout.startDate).toWorkoutDurationFormatted(
                        stringResource(R.string.hours_abbr),
                        stringResource(R.string.minutes_abbr),
                        stringResource(R.string.seconds_abbr)
                    )
                }

                workout.startDate != null && workout.endDate == null -> {
                    stringResource(id = R.string.active)
                }

                else -> stringResource(id = R.string.duration_placeholder)
            }

            Column {
                WorkoutDateRow(
                    date = workout.date?.toWorkoutDate(),
                    duration = durationText,
                    onDateClick = {},
                )
                val programLabel = workout.workoutProgramDetails?.let {
                    "${stringResource(programIdToNameId(it.id))} Week ${it.week}"
                }
                if (programLabel != null) {
                    InputChip(
                        onClick = { },
                        label = {
                            Text(
                                text = programLabel
                            )
                        },
                        selected = false,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutDateRow(
    date: String?,
    duration: String,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            date ?: stringResource(R.string.planned),
            Modifier
                .modifyIf(isEditing) {
                    clickable(onClick = onDateClick)
                },
            fontSize = 20.sp
        )
        Text(duration, fontSize = 20.sp)
    }
}

@Preview
@Composable
fun WorkoutListPreview() {
    val workouts = listOf(
        Workout(1, type = Bench, workoutProgramDetails = ProgramDetails(1, 2)),
        Workout(2, type = Deadlift),
        Workout(3, type = Squat),
        Workout(4, type = Deadlift),
        Workout(5, type = Deadlift),
        Workout(6, type = Bench),
        Workout(7, type = Squat),
    )
    WorkoutList(workoutList = workouts, activeWorkoutsList = emptyList(), onWorkoutClick = {})
}