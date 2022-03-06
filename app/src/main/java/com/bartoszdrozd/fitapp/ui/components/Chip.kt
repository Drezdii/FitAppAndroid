package com.bartoszdrozd.fitapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.body2
    ) {
        Row(
            Modifier
                .wrapContentWidth()
                .height(32.dp)
                .border(
                    BorderStroke(1.dp, LocalContentColor.current),
                    RoundedCornerShape(8.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            content()
        }
    }
}