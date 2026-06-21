package com.group4.quizapp.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.Question
import com.group4.quizapp.data.database.QuizAttemptDetail
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.database.QuizResult
import com.group4.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private val userAnswers = mutableListOf<String>()

    fun loadQuestions(category: String, difficulty: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getQuestions(category, difficulty)
            _questions.postValue(result)
        }
    }

    fun answerQuestion(selectedOption: String) {
        val currentQuestions = _questions.value ?: return
        val index = _currentIndex.value ?: 0
        
        if (index < currentQuestions.size) {
            userAnswers.add(selectedOption)
            val isCorrect = selectedOption == currentQuestions[index].correctOption
            if (isCorrect) _score.value = (_score.value ?: 0) + 1
            
            val next = index + 1
            if (next >= currentQuestions.size) {
                _quizFinished.value = true
            } else {
                _currentIndex.value = next
            }
        }
    }

    fun saveResults(category: String, difficulty: String, timeSpent: Int) {
        val questionsList = _questions.value ?: return
        val scoreValue = _score.value ?: 0
        
        viewModelScope.launch(Dispatchers.IO) {
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val today = dateFormat.format(Date())
            
            val result = QuizResult(
                category = category,
                difficulty = difficulty,
                score = scoreValue,
                totalQuestions = questionsList.size,
                dateTaken = today,
                timeSpent = timeSpent
            )
            
            val resultId = repository.insertResult(result).toInt()
            
            val details = questionsList.mapIndexed { index, question ->
                QuizAttemptDetail(
                    resultId = resultId,
                    questionText = question.questionText,
                    selectedOption = if (index < userAnswers.size) userAnswers[index] else "None",
                    correctOption = question.correctOption,
                    optionA = question.optionA,
                    optionB = question.optionB,
                    optionC = question.optionC,
                    optionD = question.optionD
                )
            }
            
            repository.insertAttemptDetails(details)
        }
    }
}
