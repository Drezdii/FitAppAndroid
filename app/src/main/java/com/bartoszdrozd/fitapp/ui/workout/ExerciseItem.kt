package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.utils.toNameResId
import kotlin.math.ceil
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseItem(exercise: Exercise, actions: IWorkoutActions, isExpanded: Boolean) {
    val exerciseNameResId = rememberSaveable { exercise.exerciseType.toNameResId() }
    val smallPadding = dimensionResource(R.dimen.small_padding)

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
                Text(
                    text = stringResource(exerciseNameResId),
                    Modifier
                        .padding(smallPadding)
                        .fillMaxWidth()
                        .clickable { actions.onClickExpand(exercise.id) }
                )

                OneRepMaxRow(exercise = exercise)

//                if (!isExpanded) {
//                    Text(
//                        text = pluralStringResource(
//                            id = R.plurals.plural_sets,
//                            count = exercise.sets.size,
//                            exercise.sets.size,
//                        ),
//                        Modifier.padding(smallPadding)
//                    )
//                }
            }
            Column {
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
                                Icons.Outlined.AddCircleOutline,
                                contentDescription = stringResource(R.string.add_set)
                            )
                        }

                        IconButton(
                            onClick = { actions.deleteExercise(exercise) }
                        ) {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = stringResource(R.string.delete_exercise)
                            )
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
    val oneRepMax = 100.0
    val biggestSet = exercise.sets.maxByOrNull { it.weight } ?: return

    val repsNeeded = ceil(
        (-(biggestSet.weight - (1.0278 * oneRepMax)) / (0.0278 * oneRepMax))
    ).toInt()

    Row(
        Modifier
            .padding(dimensionResource(id = R.dimen.small_padding))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: Use actual 1RM to calculate this
//        if (repsNeeded in 1..15) {
//            Icon(Icons.Outlined.EmojiEvents, contentDescription = null)
//
//            Text(
//                text = "Reps to beat 1RM: $repsNeeded",
//                style = MaterialTheme.typography.labelMedium
//            )
//        }

        Spacer(modifier = Modifier.weight(1f))

        if (biggestSet.reps in 1..15) {
            val estimatedMax =
                (biggestSet.weight / (1.0278 - (0.0278 * biggestSet.reps))).roundToInt()

            Text(text = "Est. Max: ${estimatedMax}kg", style = MaterialTheme.typography.labelMedium)
        }
    }
}