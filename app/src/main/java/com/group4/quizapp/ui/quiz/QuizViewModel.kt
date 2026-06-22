package com.group4.quizapp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.usecase.GetQuestionsUseCase
import com.group4.quizapp.domain.usecase.SaveQuizResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val saveQuizResultUseCase: SaveQuizResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val userAnswers = mutableListOf<String>()

    fun loadQuestions(category: String, difficulty: String) {
        viewModelScope.launch {
            val result = getQuestionsUseCase(category, difficulty)
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
            saveQuizResultUseCase(
                category = category,
                difficulty = difficulty,
                score = scoreValue,
                questions = questionsList,
                userAnswers = userAnswers,
                timeSpent = timeSpent
            )
        }
    }
}
