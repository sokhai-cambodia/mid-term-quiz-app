package com.group4.quizapp.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.QuizRepository
import com.group4.quizapp.data.model.QuizResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository.getInstance(application)
    private val query = MutableStateFlow("")

    val results: StateFlow<List<QuizResult>?> = query
        .flatMapLatest { q ->
            if (q.isBlank()) repository.getAllResults() else repository.searchResults(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun search(newQuery: String) {
        query.value = newQuery
    }

    fun loadHistory() {
        // FlatMap handles this automatically, but we can reset query
        query.value = ""
    }

    fun clearHistory() = viewModelScope.launch {
        repository.clearResults()
    }
}
