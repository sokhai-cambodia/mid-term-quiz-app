package com.group4.quizapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.domain.model.QuizResult
import com.group4.quizapp.domain.usecase.ClearHistoryUseCase
import com.group4.quizapp.domain.usecase.ObserveHistoryUseCase
import com.group4.quizapp.domain.usecase.SearchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val observeHistoryUseCase: ObserveHistoryUseCase,
    private val searchHistoryUseCase: SearchHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")

    // null = not loaded yet, distinct from "loaded and genuinely empty"
    val results: StateFlow<List<QuizResult>?> = query
        .flatMapLatest { q ->
            if (q.isBlank()) observeHistoryUseCase() else searchHistoryUseCase(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun search(newQuery: String) {
        query.value = newQuery
    }

    fun clearHistory() = viewModelScope.launch {
        clearHistoryUseCase()
    }
}
