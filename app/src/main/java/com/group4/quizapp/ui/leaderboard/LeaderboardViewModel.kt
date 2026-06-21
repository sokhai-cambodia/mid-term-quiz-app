package com.group4.quizapp.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.database.QuizResult
import com.group4.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )

    private val _topScores = MutableLiveData<List<QuizResult>>()
    val topScores: LiveData<List<QuizResult>> = _topScores

    fun loadLeaderboard() = viewModelScope.launch(Dispatchers.IO) {
        _topScores.postValue(repository.getTopScoresByCategory())
    }
}
