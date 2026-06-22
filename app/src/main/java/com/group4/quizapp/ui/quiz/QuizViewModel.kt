package com.group4.quizapp.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.QuizRepository
import com.group4.quizapp.data.model.Question
import com.group4.quizapp.data.model.QuizAttemptDetail
import com.group4.quizapp.data.model.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository.getInstance(application)

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val userAnswers = mutableListOf<String>()

    fun loadQuestions(category: String, difficulty: String) {
        viewModelScope.launch {
            val result = repository.getQuestions(category, difficulty)
            _uiState.value = _uiState.value.copy(
                questions = result,
                isLoading = false,
                errorMessage = if (result.isEmpty()) "No questions found!" else null
            )
        }
    }

    fun answerQuestion(selectedOption: String) {
        val currentState = _uiState.value
        val index = currentState.currentIndex
        
        if (index < currentState.questions.size) {
            userAnswers.add(selectedOption)
            val isCorrect = selectedOption == currentState.questions[index].correctOption
            val newScore = if (isCorrect) currentState.score + 1 else currentState.score
            
            val next = index + 1
            if (next >= currentState.questions.size) {
                _uiState.value = currentState.copy(score = newScore, isFinished = true)
            } else {
                _uiState.value = currentState.copy(score = newScore, currentIndex = next)
            }
        }
    }

    fun saveResults(category: String, difficulty: String, timeSpent: Int) {
        val currentState = _uiState.value
        val questionsList = currentState.questions
        val scoreValue = currentState.score

        viewModelScope.launch {
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
