package com.group4.quizapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.group4.quizapp.utils.PreferencesManager

class QuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        val preferencesManager = PreferencesManager(this)
        
        // Apply the saved dark mode preference once, before any Activity exists
        AppCompatDelegate.setDefaultNightMode(
            if (preferencesManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
