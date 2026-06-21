package com.group4.quizapp.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.Question
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    fun loadQuestions(category: String, difficulty: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getQuestions(category, difficulty)
            _questions.postValue(result)
        }
    }

    fun answerQuestion(isCorrect: Boolean) {
        if (isCorrect) _score.value = (_score.value ?: 0) + 1
        val next = (_currentIndex.value ?: 0) + 1
        if (next >= (_questions.value?.size ?: 0)) {
            _quizFinished.value = true
        } else {
            _currentIndex.value = next
        }
    }
}
