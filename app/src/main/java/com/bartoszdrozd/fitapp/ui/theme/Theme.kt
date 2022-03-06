package com.bartoszdrozd.fitapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

//private val DarkColorPalette = darkColors(
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200
//)
//
//private val LightColorPalette = lightColors(
//    primary = Purple500,
//    primaryVariant = Purple700,
//    secondary = Teal200
//
//    /* Other default colors to override
//    background = Color.White,
//    surface = Color.White,
//    onPrimary = Color.White,
//    onSecondary = Color.Black,
//    onBackground = Color.Black,
//    onSurface = Color.Black,
//    */
//)

@Composable
fun FitAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
//    val colors = if (darkTheme) {
//        DarkColorPalette
//    } else {
//        LightColorPalette
//    }

    val colorScheme = if (darkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

//    MaterialTheme(
//        colors = DarkColorPalette,
//        typography = Typography,
//        shapes = Shapes,
//        content = content
//    )

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography3,
        content = content
    )
}