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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutType.*
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
        item {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = { onWorkoutClick(0) }
            ) {
                Text(stringResource(R.string.add_workout))
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
                NONE -> R.drawable.ic_deadlift
                DEADLIFT -> R.drawable.ic_deadlift
                BENCH -> R.drawable.ic_bench_press
                SQUAT -> R.drawable.ic_squat
                OHP -> R.drawable.ic_ohp
            }

            Icon(
                painter = painterResource(id = workoutTypeId),
                contentDescription = null,
                Modifier
                    .padding(end = 24.dp)
                    .aspectRatio(1f)
                    .fillMaxHeight()
            )

            val durationText = if (workout.endDate != null && workout.startDate != null) {
                workout.endDate.minus(workout.startDate).toWorkoutDuration()
            } else {
                stringResource(R.string.duration_placeholder)
            }

            WorkoutDateRow(
                date = workout.date.toWorkoutDate(),
                duration = durationText,
                Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun WorkoutDateRow(date: String, duration: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(
            date,
            Modifier
                .weight(1f),
            fontSize = 24.sp
        )

        Text(duration, fontSize = 24.sp)
    }
}

@Preview
@Composable
fun WorkoutListPreview() {
    val workouts = listOf(
        Workout(1, type = BENCH),
        Workout(2, type = DEADLIFT),
        Workout(3, type = SQUAT),
        Workout(4, type = DEADLIFT),
        Workout(5, type = DEADLIFT),
        Workout(6, type = BENCH),
        Workout(7, type = SQUAT),
    )
    WorkoutList(workoutList = workouts, onWorkoutClick = {})
}