package com.group4.quizapp.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("QuizAppPrefs", Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean("darkMode", false)
        set(value) = prefs.edit().putBoolean("darkMode", value).apply()

    var timerDuration: Int
        get() = prefs.getInt("timerDuration", 30)
        set(value) = prefs.edit().putInt("timerDuration", value).apply()
}
