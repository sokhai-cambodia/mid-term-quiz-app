package com.group4.quizapp.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.domain.model.QuizResult
import com.group4.quizapp.domain.usecase.ObserveLeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    observeLeaderboardUseCase: ObserveLeaderboardUseCase
) : ViewModel() {

    val topScores: StateFlow<List<QuizResult>> = observeLeaderboardUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
