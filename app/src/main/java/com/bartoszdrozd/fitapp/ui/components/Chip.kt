package com.bartoszdrozd.fitapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.bodyMedium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth()
                .height(32.dp)
                .border(
                    BorderStroke(1.dp, LocalContentColor.current),
                    shape = MaterialTheme.shapes.small
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp)
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    Chip(onClick = { }) {
        Text(text = "Deadlift")
    }
}