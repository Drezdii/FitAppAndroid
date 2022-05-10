package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutType.*
import com.bartoszdrozd.fitapp.utils.modifyIf
import com.bartoszdrozd.fitapp.utils.toWorkoutDate
import com.bartoszdrozd.fitapp.utils.toWorkoutDuration

@Composable
fun WorkoutListScreen(viewModel: WorkoutListViewModel, onWorkoutClick: (Long) -> Unit) {
    val workouts by viewModel.workouts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWorkouts()
    }

    WorkoutList(workoutList = workouts, onWorkoutClick)
}

@Composable
fun WorkoutList(
    workoutList: List<Workout>,
    onWorkoutClick: (Long) -> Unit,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(state = scrollState) {
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
            .padding(8.dp)
            .clickable {
                onWorkoutClick(workout.id)
            }
    ) {
        Row(
            Modifier
                .padding(24.dp)
                .height(IntrinsicSize.Min)
        ) {
            val workoutTypeId = when (workout.type) {
                None -> R.drawable.ic_deadlift
                Deadlift -> R.drawable.ic_deadlift
                Bench -> R.drawable.ic_bench_press
                Squat -> R.drawable.ic_squat
                Ohp -> R.drawable.ic_ohp
            }

            Icon(
                painter = painterResource(id = workoutTypeId),
                contentDescription = null,
                Modifier
                    .padding(end = 24.dp)
                    .aspectRatio(1f)
                    .fillMaxHeight()
            )

            val durationText = when {
                workout.startDate != null && workout.endDate != null -> {
                    workout.endDate.minus(workout.startDate).toWorkoutDuration()
                }
                workout.startDate != null && workout.endDate == null -> {
                    stringResource(id = R.string.active)
                }
                else -> stringResource(id = R.string.duration_placeholder)
            }

            WorkoutDateRow(
                date = workout.date?.toWorkoutDate(),
                duration = durationText,
                onDateClick = {},
                Modifier.align(Alignment.CenterVertically)
            )
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
            date ?: "Planned",
            Modifier
                .modifyIf(isEditing) {
                    clickable(onClick = onDateClick)
                },
            fontSize = 24.sp
        )
        Text(duration, fontSize = 24.sp)
    }
}

@Preview
@Composable
fun WorkoutListPreview() {
    val workouts = listOf(
        Workout(1, type = Bench),
        Workout(2, type = Deadlift),
        Workout(3, type = Squat),
        Workout(4, type = Deadlift),
        Workout(5, type = Deadlift),
        Workout(6, type = Bench),
        Workout(7, type = Squat),
    )
    WorkoutList(workoutList = workouts, onWorkoutClick = {})
}