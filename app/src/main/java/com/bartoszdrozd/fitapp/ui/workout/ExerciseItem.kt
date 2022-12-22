package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.utils.toNameResId
import kotlin.math.roundToInt

@Composable
fun ExerciseItem(exercise: Exercise, actions: IWorkoutActions) {
    val exerciseNameResId = rememberSaveable { exercise.exerciseType.toNameResId() }
    val smallPadding = dimensionResource(R.dimen.small_padding)
    // Only expand by default for empty exercises
    var isExpanded by rememberSaveable { mutableStateOf(exercise.sets.isEmpty()) }

    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(vertical = smallPadding)
    ) {
        Column(
            Modifier
                .padding(smallPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                Row(Modifier.padding(smallPadding)) {
                    Text(
                        text = stringResource(exerciseNameResId),
                        Modifier
                            .weight(1f)
                            .clickable {
                                // Don't allow closing empty exercises
                                if (exercise.sets.isNotEmpty()) {
                                    isExpanded = !isExpanded
                                }
                            }
                    )

                    // Show number of completed sets or a completion icon
                    if (!isExpanded && exercise.sets.isNotEmpty()) {
                        val numOfCompleted = exercise.sets.count { it.completed }

                        if (numOfCompleted == exercise.sets.size) {
                            Icon(Icons.Outlined.Check, contentDescription = null)
                        } else {
                            Text(text = "${numOfCompleted}/${exercise.sets.size}")
                        }
                    }
                }
                if (isExpanded) {
                    OneRepMaxRow(exercise = exercise)
                }
            }
            Column {
                if (isExpanded) {
                    exercise.sets.forEach { set ->
                        WorkoutSetItem(
                            set,
                            updateSet = { updatedSet ->
                                actions.updateSet(
                                    updatedSet,
                                    exercise.id
                                )
                            },
                            deleteSet = {
                                actions.deleteSet(it, exercise.id)
                            }
                        )
                    }
                    Surface(
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .wrapContentWidth()
                                .border(
                                    BorderStroke(1.dp, LocalContentColor.current),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = smallPadding),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { actions.addSet(exercise) }
                            ) {
                                Icon(
                                    Icons.Outlined.AddCircle,
                                    contentDescription = stringResource(R.string.add_set)
                                )
                            }

                            IconButton(
                                onClick = { actions.deleteExercise(exercise) }
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.delete_exercise)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OneRepMaxRow(exercise: Exercise) {
    // Test with 1RM = 100kg
    // TODO: Use actual 1RM to calculate this
    val oneRepMax = 100.0
    val biggestSet =
        exercise.sets.maxByOrNull { (it.weight / (1.0278 - (0.0278 * it.reps))).roundToInt() }
            ?: return

//    val repsNeeded = ceil(
//        (-(biggestSet.weight - (1.0278 * oneRepMax)) / (0.0278 * oneRepMax))
//    ).toInt()

    Row(
        Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        if (biggestSet.reps in 1..15) {
            val estimatedMax =
                (biggestSet.weight / (1.0278 - (0.0278 * biggestSet.reps))).roundToInt()

            Text(text = stringResource(R.string.estimated_max, "${estimatedMax}kg"), style = MaterialTheme.typography.labelMedium)
        }
    }
}