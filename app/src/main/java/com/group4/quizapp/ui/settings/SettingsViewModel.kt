package com.group4.quizapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.repository.QuizRepository
import com.group4.quizapp.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )
    private val prefs = PreferencesManager(application)

    val isDarkMode get() = prefs.isDarkMode
    val timerDuration get() = prefs.timerDuration

    fun setDarkMode(enabled: Boolean) { prefs.isDarkMode = enabled }
    fun setTimerDuration(seconds: Int) { prefs.timerDuration = seconds }

    fun clearHistory() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearResults()
    }
}
