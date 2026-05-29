package com.deoony.app.ui.util

import androidx.compose.ui.graphics.Color

fun safeParseColor(colorHex: String, fallbackColor: Color = Color(0xFF6750A4)): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        fallbackColor
    }
}
