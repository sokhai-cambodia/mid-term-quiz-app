package com.group4.quizapp.ui.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.database.QuizResult
import com.group4.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )

    fun saveResult(result: QuizResult) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertResult(result)
    }
}
