package com.group4.quizapp.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.QuizRepository
import com.group4.quizapp.data.model.QuizResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository.getInstance(application)

    val topScores: StateFlow<List<QuizResult>> = repository.getTopScoresByCategory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadLeaderboard() {
        // Handled by StateFlow
    }
}
