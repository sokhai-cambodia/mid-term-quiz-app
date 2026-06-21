package com.group4.quizapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.usecase.GetAttemptDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizDetailViewModel @Inject constructor(
    private val getAttemptDetailsUseCase: GetAttemptDetailsUseCase
) : ViewModel() {

    private val _details = MutableStateFlow<List<QuizAttemptDetail>>(emptyList())
    val details: StateFlow<List<QuizAttemptDetail>> = _details.asStateFlow()

    fun loadDetails(resultId: Int) {
        viewModelScope.launch {
            _details.value = getAttemptDetailsUseCase(resultId)
        }
    }
}
