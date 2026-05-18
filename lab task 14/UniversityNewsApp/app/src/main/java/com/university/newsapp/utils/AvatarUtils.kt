package com.university.newsapp.utils

import android.graphics.Color
import kotlin.math.abs

object AvatarUtils {

    private val avatarColors = intArrayOf(
        Color.parseColor("#1565C0"),
        Color.parseColor("#2E7D32"),
        Color.parseColor("#6A1B9A"),
        Color.parseColor("#C62828"),
        Color.parseColor("#EF6C00"),
        Color.parseColor("#00838F"),
        Color.parseColor("#4527A0"),
        Color.parseColor("#AD1457")
    )

    fun getInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "${parts[0].first()}${parts[1].first()}".uppercase()
        }
    }

    fun getAvatarColor(seed: String): Int {
        val index = abs(seed.hashCode()) % avatarColors.size
        return avatarColors[index]
    }
}
