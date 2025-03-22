package com.example.eveningattendanceportal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = android.graphics.Color.parseColor("#6200EE"),
    secondary = android.graphics.Color.parseColor("#03DAC5")
)

private val DarkColors = darkColorScheme(
    primary = android.graphics.Color.parseColor("#BB86FC"),
    secondary = android.graphics.Color.parseColor("#03DAC5")
)

@Composable
fun EveningAttendancePortalTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors =
        if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
