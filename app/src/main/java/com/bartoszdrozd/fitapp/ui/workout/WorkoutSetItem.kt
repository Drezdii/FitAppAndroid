package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet

@Composable
fun WorkoutSetItem(
    set: WorkoutSet,
    updateSet: (WorkoutSet) -> Unit,
    deleteSet: (WorkoutSet) -> Unit
) {
    val smallPadding = dimensionResource(R.dimen.small_padding)

    var reps by remember(set.id) { mutableStateOf(set.reps.toString()) }
    var weight by remember(set.id) { mutableStateOf(set.weight.toString()) }

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = smallPadding)
    ) {
        Checkbox(checked = set.completed,
            onCheckedChange = {
                updateSet(
                    set.copy(completed = !set.completed)
                )
            })

        OutlinedTextField(
            value = reps,
            onValueChange = { repsValue ->
                reps = repsValue
                updateSet(set.copy(reps = repsValue.toIntOrNull() ?: 0))
            },
            Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        Column(
            Modifier
                .height(IntrinsicSize.Min)
                .padding(smallPadding)
                .align(alignment = Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "x"
            )
        }

        OutlinedTextField(
            value = weight,
            onValueChange = { weightValue ->
                weight = weightValue
                updateSet(set.copy(weight = weightValue.toDoubleOrNull() ?: 0.0))
            },
            Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        IconButton(onClick = { deleteSet(set) }, Modifier.align(Alignment.CenterVertically)) {
            Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
        }
    }
}