package com.group4.quizapp.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.QuizRepository
import com.group4.quizapp.data.model.QuizAttemptDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository.getInstance(application)

    private val _details = MutableStateFlow<List<QuizAttemptDetail>>(emptyList())
    val details: StateFlow<List<QuizAttemptDetail>> = _details.asStateFlow()

    fun loadDetails(resultId: Int) {
        viewModelScope.launch {
            _details.value = repository.getAttemptDetails(resultId)
        }
    }
}
