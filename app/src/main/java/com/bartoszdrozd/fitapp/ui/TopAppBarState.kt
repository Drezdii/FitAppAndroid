package com.bartoszdrozd.fitapp.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopAppBarState(
    val title: @Composable () -> Unit,
    val actions: @Composable RowScope.() -> Unit,
    val displayActions: Boolean = true,
    val showBackButton: Boolean = false
)
