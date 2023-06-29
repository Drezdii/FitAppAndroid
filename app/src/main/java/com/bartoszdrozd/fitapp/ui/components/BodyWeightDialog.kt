package com.bartoszdrozd.fitapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.model.stats.BodyWeightEntry
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayAt

@OptIn(ExperimentalMaterial3Api::class)
class NoFutureDatesSelectable : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return Clock.System.todayAt(TimeZone.currentSystemDefault()).atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds() >= utcTimeMillis
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyWeightDialog(
    onDismiss: () -> Unit,
    onConfirm: (BodyWeightEntry) -> Unit,
    startValue: Float = 0f
) {
    var openDatePicker by remember { mutableStateOf(false) }

    val today = Clock.System.todayAt(TimeZone.currentSystemDefault())

    val datePickerState = rememberDatePickerState(
        today.atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds(),
        selectableDates = NoFutureDatesSelectable()
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it)
            .toLocalDateTime(
                TimeZone.UTC
            ).date
    }

    var bodyWeight by remember { mutableFloatStateOf(startValue) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    Text(
                        text = "${
                            if (today == selectedDate) stringResource(R.string.today) else selectedDate ?: R.string.select_date
                        }",
                        fontSize = dimensionResource(R.dimen.title_size).value.sp,
                        modifier = Modifier.clickable { openDatePicker = true }
                    )
                    IconButton(onClick = { openDatePicker = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit body weight entry date")
                    }
                }

                if (openDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { openDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = { openDatePicker = false }) {
                                Text(stringResource(R.string.save))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openDatePicker = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }) {
                        DatePicker(state = datePickerState)
                    }
                }

                Row {
                    OutlinedTextField(
                        value = bodyWeight.toString(),
                        onValueChange = { bw ->
                            bodyWeight = bw.toFloat()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(vertical = 4.dp),
                        suffix = { Text(stringResource(R.string.kg_unit)) }
                    )
                }

                Row {
                    TextButton(onClick = { onDismiss() }) {
                        Text(stringResource(R.string.cancel))
                    }

                    TextButton(onClick = {
                        selectedDate?.let {
                            onConfirm(BodyWeightEntry(0, it, bodyWeight))
                        }
                    }) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}