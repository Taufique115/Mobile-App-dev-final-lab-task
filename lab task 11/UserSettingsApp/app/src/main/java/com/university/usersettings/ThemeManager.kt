package com.university.usersettings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    fun applyFromPrefs(context: Context) {
        val mode = nightModeFromPrefs(context)
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    fun nightModeFromPrefs(context: Context): Int {
        val key = context.getSharedPreferences(Constants.PREFS_APP, Context.MODE_PRIVATE)
            .getString(Constants.KEY_THEME, "light") ?: "light"
        return when (key) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "system" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
    }
}
