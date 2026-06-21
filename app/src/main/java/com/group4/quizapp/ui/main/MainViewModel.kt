package com.group4.quizapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.DatabaseSeeder
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )

    fun seedDatabase() = viewModelScope.launch(Dispatchers.IO) {
        DatabaseSeeder.seedDatabase(QuizDatabase.getDatabase(getApplication()))
    }
}
