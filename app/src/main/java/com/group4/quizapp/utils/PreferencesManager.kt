package com.group4.quizapp.utils

import android.content.Context

class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("QuizAppPrefs", Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean("darkMode", false)
        set(value) = prefs.edit().putBoolean("darkMode", value).apply()

    var timerDuration: Int
        get() = prefs.getInt("timerDuration", 30)
        set(value) = prefs.edit().putInt("timerDuration", value).apply()
}
