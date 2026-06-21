package com.group4.quizapp.ui.history

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

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )

    private val _results = MutableLiveData<List<QuizResult>>()
    val results: LiveData<List<QuizResult>> = _results

    fun loadHistory() = viewModelScope.launch(Dispatchers.IO) {
        _results.postValue(repository.getAllResults())
    }

    fun search(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _results.postValue(repository.searchResults(query))
    }

    fun clearHistory() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearResults()
        _results.postValue(emptyList())
    }
}
