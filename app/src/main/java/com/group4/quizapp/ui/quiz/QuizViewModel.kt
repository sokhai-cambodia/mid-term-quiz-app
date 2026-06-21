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

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val saveQuizResultUseCase: SaveQuizResultUseCase
) : ViewModel() {

    // null = not loaded yet, distinct from "loaded and genuinely empty"
    private val _questions = MutableStateFlow<List<Question>?>(null)
    val questions: StateFlow<List<Question>?> = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished.asStateFlow()

    private val userAnswers = mutableListOf<String>()

    fun loadQuestions(category: String, difficulty: String) {
        viewModelScope.launch {
            _questions.value = getQuestionsUseCase(category, difficulty)
        }
    }

    fun answerQuestion(selectedOption: String) {
        val currentQuestions = _questions.value ?: return
        val index = _currentIndex.value

        if (index < currentQuestions.size) {
            userAnswers.add(selectedOption)
            val isCorrect = selectedOption == currentQuestions[index].correctOption
            if (isCorrect) _score.value = _score.value + 1

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
        val scoreValue = _score.value

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
