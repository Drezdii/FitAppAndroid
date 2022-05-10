package com.bartoszdrozd.fitapp.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.ui.graphics.vector.ImageVector
import com.bartoszdrozd.fitapp.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Timeline : Screen("timeline", R.string.timeline, Icons.Outlined.Dashboard)
    object Creator : Screen("creator", R.string.creator, Icons.Outlined.Create)
}
