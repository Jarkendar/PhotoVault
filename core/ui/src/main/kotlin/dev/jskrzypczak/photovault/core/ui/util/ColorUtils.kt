package dev.jskrzypczak.photovault.core.ui.util

import androidx.compose.ui.graphics.Color

fun photoPlaceholderColor(id: String): Color {
    val hue = ((id.hashCode() * 137.508f) % 360f + 360f) % 360f
    return Color.hsl(hue, saturation = 0.4f, lightness = 0.6f)
}

fun parseHexColor(hex: String): Color {
    val cleaned = hex.trimStart('#')
    return try {
        when (cleaned.length) {
            6 -> Color((0xFF000000L or cleaned.toLong(16)).toInt())
            8 -> Color(cleaned.toLong(16).toInt())
            else -> Color.Gray
        }
    } catch (e: NumberFormatException) {
        Color.Gray
    }
}
