package com.bartoszdrozd.fitapp.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.program.ProgramType
import com.bartoszdrozd.fitapp.model.program.ProgramValues
import com.bartoszdrozd.fitapp.model.stats.PersonalBest
import com.bartoszdrozd.fitapp.model.workout.ExerciseType
import com.bartoszdrozd.fitapp.ui.creator.CreatorViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt
import kotlin.math.roundToInt

@Composable
fun Program531BBB4DaysScreen(creatorViewModel: CreatorViewModel) {
    var deadlift1RM by remember { mutableStateOf("0") }
    var bench1RM by remember { mutableStateOf("0") }
    var squat1RM by remember { mutableStateOf("0") }
    var ohp1RM by remember { mutableStateOf("0") }
    var trainingMaxPercentage by remember { mutableFloatStateOf(85F) }

    val smallPadding = dimensionResource(R.dimen.small_padding)

    fun onOneRepMaxChanged() {
        val today = Clock.System.todayAt(TimeZone.currentSystemDefault())

        val config = ProgramValues(
            ProgramType.BBB_531_4_Days,
            listOf(
                PersonalBest(ExerciseType.Deadlift, deadlift1RM.toFloatOrNull() ?: 0f, today),
                PersonalBest(ExerciseType.Bench, bench1RM.toFloatOrNull() ?: 0f, today),
                PersonalBest(ExerciseType.Squat, squat1RM.toFloatOrNull() ?: 0f, today),
                PersonalBest(ExerciseType.Ohp, ohp1RM.toFloatOrNull() ?: 0f, today)
            ),
            trainingMax = trainingMaxPercentage.roundToInt() / 100F
        )

        creatorViewModel.createWorkouts(config)
    }

    LaunchedEffect(Unit) {
        onOneRepMaxChanged()
    }

    Column(
        Modifier
            .padding(horizontal = smallPadding)
    ) {
        Text(text = stringResource(id = R.string.your_maxes), fontSize = 16.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(smallPadding)) {
            OutlinedTextField(
                value = deadlift1RM,
                onValueChange = { value ->
                    deadlift1RM = value
                    onOneRepMaxChanged()
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.deadlift)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = bench1RM,
                onValueChange = { value ->
                    bench1RM = value
                    onOneRepMaxChanged()
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.bench)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = squat1RM,
                onValueChange = { value ->
                    squat1RM = value
                    onOneRepMaxChanged()
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.squat)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = ohp1RM,
                onValueChange = { value ->
                    ohp1RM = value
                    onOneRepMaxChanged()
                },
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
                onValueChange = { value ->
                    trainingMaxPercentage = value
                    onOneRepMaxChanged()
                },
                valueRange = 50f..100f,
                modifier = Modifier.weight(1f)
            )

            Text(text = "${trainingMaxPercentage.roundToInt()}%", modifier = Modifier.weight(0.2f))
        }
    }
}