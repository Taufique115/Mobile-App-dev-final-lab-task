package com.university.usersettings

import android.app.Application

class UserSettingsApplication : Application() {

    override fun onCreate() {
        ThemeManager.applyFromPrefs(this)
        super.onCreate()
    }
}
