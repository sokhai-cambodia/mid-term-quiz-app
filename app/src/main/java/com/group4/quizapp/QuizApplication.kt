package com.group4.quizapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.group4.quizapp.utils.PreferencesManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class QuizApplication : Application() {

    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate() // Hilt injects preferencesManager here

        // Apply the saved dark mode preference once, before any Activity exists,
        // so no Activity has to recreate itself due to a night-mode change.
        AppCompatDelegate.setDefaultNightMode(
            if (preferencesManager.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
