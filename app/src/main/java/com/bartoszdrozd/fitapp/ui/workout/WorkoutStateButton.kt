package com.bartoszdrozd.fitapp.ui.workout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bartoszdrozd.fitapp.R

@Composable
fun WorkoutStateButton(onClick: () -> Unit, isStarted: Boolean) {
    FilledTonalButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Box(modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)) {
            if (!isStarted) {
                Text(text = stringResource(id = R.string.start))
            } else {
                Text(text = stringResource(id = R.string.finish))
            }
        }
    }
}

@Preview
@Composable
fun WorkoutStateButtonPreview() {
    WorkoutStateButton(onClick = { }, isStarted = false)
}