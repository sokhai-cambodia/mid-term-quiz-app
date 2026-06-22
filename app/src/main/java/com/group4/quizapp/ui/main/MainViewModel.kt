package com.group4.quizapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.QuizRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository.getInstance(application)

    fun seedDatabase() = viewModelScope.launch {
        repository.seedDatabase()
    }
}
