package com.bartoszdrozd.fitapp.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.program.ProgramType
import com.bartoszdrozd.fitapp.model.program.ProgramValues
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.model.stats.OneRepMax
import com.bartoszdrozd.fitapp.ui.creator.CreatorViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program531BBB4DaysScreen(creatorViewModel: CreatorViewModel) {
    var deadlift1RM by remember { mutableStateOf("0") }
    var bench1RM by remember { mutableStateOf("0") }
    var squat1RM by remember { mutableStateOf("0") }
    var ohp1RM by remember { mutableStateOf("0") }
    var trainingMaxPercentage by remember { mutableStateOf(85F) }

    val smallPadding = dimensionResource(R.dimen.small_padding)

    Column(
        Modifier
            .padding(horizontal = smallPadding)
    ) {
        Text(text = stringResource(id = R.string.your_maxes), fontSize = 16.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(smallPadding)) {
            OutlinedTextField(
                value = deadlift1RM,
                onValueChange = { value -> deadlift1RM = value },
                label = {
                    Text(
                        text = stringResource(id = R.string.deadlift)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = bench1RM,
                onValueChange = { value -> bench1RM = value },
                label = {
                    Text(
                        text = stringResource(id = R.string.bench)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = squat1RM,
                onValueChange = { value -> squat1RM = value },
                label = {
                    Text(
                        text = stringResource(id = R.string.squat)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = ohp1RM,
                onValueChange = { value -> ohp1RM = value },
                label = {
                    Text(
                        text = stringResource(id = R.string.ohp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        Text(text = stringResource(id = R.string.training_max_percentage))
        Row {
            Slider(
                value = trainingMaxPercentage,
                onValueChange = { value -> trainingMaxPercentage = value },
                valueRange = 50f..100f,
                modifier = Modifier.weight(1f)
            )

            Text(text = "${trainingMaxPercentage.roundToInt()}%", modifier = Modifier.weight(0.2f))
        }

        Button(onClick = {
            val config = ProgramValues(
                ProgramType.BBB_531_4_Days,
                listOf(
                    OneRepMax(ExerciseType.Deadlift, deadlift1RM.toFloat()),
                    OneRepMax(ExerciseType.Bench, bench1RM.toFloat()),
                    OneRepMax(ExerciseType.Squat, squat1RM.toFloat()),
                    OneRepMax(ExerciseType.Ohp, ohp1RM.toFloat())
                ),
                trainingMax = trainingMaxPercentage.roundToInt() / 100F
            )

            creatorViewModel.createWorkouts(config)
        }) {
            Text(text = "Create workouts")
        }
    }
}