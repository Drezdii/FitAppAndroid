package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSetItem(
    set: WorkoutSet,
    updateSet: (WorkoutSet) -> Unit,
    deleteSet: (WorkoutSet) -> Unit
) {
    val smallPadding = dimensionResource(R.dimen.small_padding)
    var reps by remember { mutableStateOf(set.reps.toString()) }
    var weight by remember { mutableStateOf<String?>(set.weight.toString()) }

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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = LocalContentColor.current,
                unfocusedBorderColor = LocalContentColor.current
            )
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
            value = set.weight.toString(),
            onValueChange = { weightValue ->
                weight = weightValue
                updateSet(set.copy(weight = weightValue.toDoubleOrNull() ?: 0.0))
            },
            Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = LocalContentColor.current,
                unfocusedBorderColor = LocalContentColor.current
            )
        )

        IconButton(onClick = { deleteSet(set) }, Modifier.align(Alignment.CenterVertically)) {
            Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
        }
    }
}