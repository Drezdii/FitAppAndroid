package com.bartoszdrozd.fitapp.ui

import androidx.annotation.StringRes
import com.bartoszdrozd.fitapp.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Timeline : Screen("timeline", R.string.timeline)
}
