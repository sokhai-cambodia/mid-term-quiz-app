package com.group4.quizapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.domain.usecase.ClearHistoryUseCase
import com.group4.quizapp.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    val isDarkMode get() = preferencesManager.isDarkMode
    val timerDuration get() = preferencesManager.timerDuration

    fun setDarkMode(enabled: Boolean) { preferencesManager.isDarkMode = enabled }
    fun setTimerDuration(seconds: Int) { preferencesManager.timerDuration = seconds }

    fun clearHistory() = viewModelScope.launch {
        clearHistoryUseCase()
    }
}
