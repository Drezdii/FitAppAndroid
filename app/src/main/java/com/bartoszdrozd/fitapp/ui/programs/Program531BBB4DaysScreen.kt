package com.bartoszdrozd.fitapp.ui.programs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.ui.creator.CreatorViewModel

@Composable
fun Program531BBB4DaysScreen(creatorViewModel: CreatorViewModel) {
    Column {
        val workouts = listOf(
            Workout(),
            Workout(),
            Workout(),
            Workout(),
            Workout(),
            Workout(),
            Workout(),
            Workout(),
            Workout()
        )
        Text(text = "5/3/1 BBB 4 Days")
        Button(onClick = { creatorViewModel.setWorkouts(workouts) }) {
            Text(text = "Create workouts")
        }
    }
}