package com.group4.quizapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.QuizRepository
import com.group4.quizapp.utils.PreferencesManager
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository.getInstance(application)
    private val preferencesManager = PreferencesManager(application)

    val isDarkMode get() = preferencesManager.isDarkMode
    val timerDuration get() = preferencesManager.timerDuration

    fun setDarkMode(enabled: Boolean) { preferencesManager.isDarkMode = enabled }
    fun setTimerDuration(seconds: Int) { preferencesManager.timerDuration = seconds }

    fun clearHistory() = viewModelScope.launch {
        repository.clearResults()
    }
}
